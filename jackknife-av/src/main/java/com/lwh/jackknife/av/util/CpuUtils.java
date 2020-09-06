/*
 * Copyright (C) 2019 The JackKnife Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lwh.jackknife.av.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

public class CpuUtils {

    private final static String TAG = "CpuUtils";

    private static boolean mCompatible = false;
    private static String mErrorMsg;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static String[] getABIList21() {
        final String[] abis = Build.SUPPORTED_ABIS;
        if (abis == null || abis.length == 0) {
            return getABIList();
        }
        return abis;
    }

    private static String[] getABIList() {
        final String[] abis = new String[2];
        abis[0] = Build.CPU_ABI;
        abis[1] = Build.CPU_ABI2;
        return abis;
    }

    private static MachineSpec machineSpec = null;

    public static boolean hasCompatibleCPU(Context context, String soLibName) {
        // If already checked return cached result
        if (mErrorMsg != null || mCompatible) {
            return mCompatible;
        }
        mCompatible = true;
        boolean hasNeon = false, hasFpu = false, hasArmV6 = false, hasPlaceHolder = false,
                hasArmV7 = false, hasMips = false, hasX86 = false, is64bits = false, isIntel = false;
        float bogoMIPS = -1;
        int processors = 0;

        /* ABI */
        String[] abis;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abis = getABIList21();
        } else {
            abis = getABIList();
        }

        for (String abi : abis) {
            Log.i(TAG, "abi=" + abi);
            if (abi.equals("x86")) {
                hasX86 = true;
            } else if (abi.equals("x86_64")) {
                hasX86 = true;
                is64bits = true;
            } else if (abi.equals("armeabi-v7a")) {
                hasArmV7 = true;
                hasArmV6 = true; /* Armv7 is backwards compatible to < v6 */
            } else if (abi.equals("armeabi")) {
                hasArmV6 = true;
            } else if (abi.equals("arm64-v8a")) {
                hasNeon = true;
                hasArmV6 = true;
                hasArmV7 = true;
                is64bits = true;
            }
        }

        /* Elf */
        ElfData elf = null;
        boolean elfHasX86 = false;
        boolean elfHasArm = false;
        boolean elfHasMips = false;
        boolean elfIs64bits = false;
        final File lib = searchLibrary(context, soLibName);
        if (lib != null && (elf = readLib(lib)) != null) {
            elfHasX86 = elf.e_machine == EM_386 || elf.e_machine == EM_X86_64;
            elfHasArm = elf.e_machine == EM_ARM || elf.e_machine == EM_AARCH64;
            elfHasMips = elf.e_machine == EM_MIPS;
            elfIs64bits = elf.is64bits;
            Log.i(TAG, "elf= " + elf.toString());
        } else {
            Log.i(TAG, "WARNING: Unable to read " + soLibName + ".so; cannot check device ABI!");
        }

        /* cpuinfo */
        FileReader fileReader = null;
        BufferedReader br = null;
        try {
            fileReader = new FileReader("/proc/cpuinfo");
            br = new BufferedReader(fileReader);
            String line;
            while ((line = br.readLine()) != null) {
                Log.i(TAG, "cpuinfo=" + line);
//                if (line.contains("vfpv4") || line.contains("idiva") || line.contains("idivt")) {
//                } else {
//                    errorMsg = "cpu太老了 不支持 没有指令 vfpv4 idiva idivt";
//                }
                if (line.contains("AArch64")) {
                    hasArmV7 = true;
                    hasArmV6 = true; /* Armv8 is backwards compatible to < v7 */
                } else if (line.contains("ARMv7")) {
                    hasArmV7 = true;
                    hasArmV6 = true; /* Armv7 is backwards compatible to < v6 */
                } else if (line.contains("ARMv6")) {
                    hasArmV6 = true;
                }
                    // "clflush size" is a x86-specific cpuinfo tag.
                    // (see kernel sources arch/x86/kernel/cpu/proc.c)
                else if (line.contains("clflush size")) {
                    hasX86 = true;
                } else if (line.contains("GenuineIntel")) {
                    hasX86 = true;
                } else if (line.contains("placeholder")) {
                    hasPlaceHolder = true;
                } else if (line.contains("CPU implementer") && line.contains("0x69")) {
                    isIntel = true;
                }
                    // "microsecond timers" is specific to MIPS.
                    // see arch/mips/kernel/proc.c
                else if (line.contains("microsecond timers")) {
                    hasMips = true;
                }
                if (line.contains("neon") || line.contains("asimd")) {
                    hasNeon = true;
                }
                if (line.contains("vfp") || (line.contains("Features") && line.contains("fp"))) {
                    hasFpu = true;
                }
                if (line.startsWith("processor")) {
                    processors++;
                }
                if (bogoMIPS < 0 && line.toLowerCase(Locale.ENGLISH).contains("bogomips")) {
                    String[] bogo_parts = line.split(":");
                    try {
                        bogoMIPS = Float.parseFloat(bogo_parts[1].trim());
                    } catch (NumberFormatException e) {
                        bogoMIPS = -1; // invalid bogomips
                    }
                }
            }
        } catch (IOException ignored) {
        } finally {
            close(br);
            close(fileReader);
        }
        if (processors == 0) {
            processors = 1; // possibly borked cpuinfo?
        }


        /* compare ELF with ABI/cpuinfo */
        if (elf != null) {
            // Enforce proper architecture to prevent problems
            if (elfHasX86 && !hasX86) {
                //Some devices lie on their /proc/cpuinfo
                // they seem to have a 'Hardware	: placeholder' property
                if (hasPlaceHolder && isIntel) {
                    Log.i(TAG, "Emulated armv7 detected, trying to launch x86 libraries");
                } else {
                    mErrorMsg = "x86 build on non-x86 device";
                    mCompatible = false;
                }
            } else if (elfHasArm && !hasArmV6) {
                mErrorMsg = "ARM build on non ARM device";
                mCompatible = false;
            }

            if (elfHasMips && !hasMips) {
                mErrorMsg = "MIPS build on non-MIPS device";
                mCompatible = false;
            } else if (elfHasArm && hasMips) {
                mErrorMsg = "ARM build on MIPS device";
                mCompatible = false;
            }

            if (elf.e_machine == EM_ARM && elf.att_arch.startsWith("v7") && !hasArmV7) {
                mErrorMsg = "ARMv7 build on non-ARMv7 device";
                mCompatible = false;
            }
            if (elf.e_machine == EM_ARM) {
                if (elf.att_arch.startsWith("v6") && !hasArmV6) {
                    mErrorMsg = "ARMv6 build on non-ARMv6 device";
                    mCompatible = false;
                } else if (elf.att_fpu && !hasFpu) {
                    mErrorMsg = "FPU-enabled build on non-FPU device";
                    mCompatible = false;
                }
            }
            if (elfIs64bits && !is64bits) {
                mErrorMsg = "64bits build on 32bits device";
                mCompatible = false;
            }
        }


        float frequency = -1;
        fileReader = null;
        br = null;
        String line = "";
        try {
            fileReader = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
            br = new BufferedReader(fileReader);
            line = br.readLine();
            if (line != null) {
                frequency = Float.parseFloat(line) / 1000.f; /* Convert to MHz */
            }
        } catch (IOException ex) {
            Log.w(TAG, "Could not find maximum CPU frequency!");
        } catch (NumberFormatException e) {
            Log.w(TAG, "Could not parse maximum CPU frequency!");
            Log.w(TAG, "Failed to parse: " + line);
        } finally {
            close(br);
            close(fileReader);
        }

        // Store into MachineSpecs
        machineSpec = new MachineSpec();
        machineSpec.hasArmV6 = hasArmV6;
        machineSpec.hasArmV7 = hasArmV7;
        machineSpec.hasFpu = hasFpu;
        machineSpec.hasMips = hasMips;
        machineSpec.hasNeon = hasNeon;
        machineSpec.hasX86 = hasX86;
        machineSpec.is64bits = is64bits;
        machineSpec.bogoMIPS = bogoMIPS;
        machineSpec.processors = processors;
        machineSpec.frequency = frequency;

        Log.i(TAG, machineSpec.toString());

        return mCompatible;
    }

    public static MachineSpec getMachineSpec() {
        return machineSpec;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static File searchLibrary(Context context, String libName) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        // Search for library path
        String[] libraryPaths;
        if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            final String property = System.getProperty("java.library.path");
            libraryPaths = property.split(":");
        } else {
            libraryPaths = new String[1];
            libraryPaths[0] = applicationInfo.nativeLibraryDir;
        }
        Log.i(TAG, "find library path=" + libraryPaths[0]);
        if (libraryPaths[0] == null) {
            Log.i(TAG, "can't find library path");
            return null;
        }
        getFileList(libraryPaths[0]);
        // Search for libjknfffmpeg.so
        File lib;
        for (String libraryPath : libraryPaths) {
            lib = new File(libraryPath, libName);
            if (lib.exists() && lib.canRead()) {
                return lib;
            }
        }
        Log.i(TAG, "WARNING: Can't find shared library");
        return null;
    }

    private static final int EM_386 = 3;
    private static final int EM_MIPS = 8;
    private static final int EM_ARM = 40;
    private static final int EM_X86_64 = 62;
    private static final int EM_AARCH64 = 183;
    private static final int ELF_HEADER_SIZE = 52;
    private static final int SECTION_HEADER_SIZE = 40;
    private static final int SHT_ARM_ATTRIBUTES = 0x70000003;

    private static class ElfData {
        ByteOrder order;
        boolean is64bits;
        int e_machine;
        int e_shoff;
        int e_shnum;
        int sh_offset;
        int sh_size;
        String att_arch;
        boolean att_fpu;

        @Override
        public String toString() {
            return "ElfData{" +
                    "order=" + order +
                    ", is64bits=" + is64bits +
                    ", e_machine=" + e_machine +
                    ", e_shoff=" + e_shoff +
                    ", e_shnum=" + e_shnum +
                    ", sh_offset=" + sh_offset +
                    ", sh_size=" + sh_size +
                    ", att_arch='" + att_arch + '\'' +
                    ", att_fpu=" + att_fpu +
                    '}';
        }
    }

    public static class MachineSpec {
        public boolean hasNeon;
        public boolean hasFpu;
        public boolean hasArmV6;
        public boolean hasArmV7;
        public boolean hasMips;
        public boolean hasX86;
        public boolean is64bits;
        public float bogoMIPS;
        public int processors;
        public float frequency; /* in MHz */

        @Override
        public String toString() {
            return "MachineSpecs{" +
                    "hasNeon=" + hasNeon +
                    ", hasFpu=" + hasFpu +
                    ", hasArmV6=" + hasArmV6 +
                    ", hasArmV7=" + hasArmV7 +
                    ", hasMips=" + hasMips +
                    ", hasX86=" + hasX86 +
                    ", is64bits=" + is64bits +
                    ", bogoMIPS=" + bogoMIPS +
                    ", processors=" + processors +
                    ", frequency=" + frequency +
                    '}';
        }
    }

    private static void getFileList(String strPath) {
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    Log.i(TAG, "DirectoryName=" + fileName);
                    getFileList(files[i].getAbsolutePath()); // 获取文件绝对路径
                } else { // 判断文件名是否以.avi结尾
                    Log.i(TAG, ".so Name=" + fileName);
                }
            }
        }
    }

    /**
     * '*' prefix means it's unsupported
     */
    private final static String[] CPU_archs = {"*Pre-v4", "*v4", "*v4T",
            "v5T", "v5TE", "v5TEJ",
            "v6", "v6KZ", "v6T2", "v6K", "v7",
            "*v6-M", "*v6S-M", "*v7E-M", "*v8"};

    private static ElfData readLib(File file) {
        RandomAccessFile in = null;
        try {
            in = new RandomAccessFile(file, "r");

            ElfData elf = new ElfData();
            if (!readHeader(in, elf)) {
                return null;
            }

            switch (elf.e_machine) {
                case EM_386:
                case EM_MIPS:
                case EM_X86_64:
                case EM_AARCH64:
                    return elf;
                case EM_ARM:
                    in.close();
                    in = new RandomAccessFile(file, "r");
                    if (!readSection(in, elf)) {
                        return null;
                    }
                    in.close();
                    in = new RandomAccessFile(file, "r");
                    if (!readArmAttributes(in, elf)) {
                        return null;
                    }
                    break;
                default:
                    return null;
            }
            return elf;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(in);
        }
        return null;
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static boolean readHeader(RandomAccessFile in, ElfData elf) throws IOException {
        // http://www.sco.com/developers/gabi/1998-04-29/ch4.eheader.html
        byte[] bytes = new byte[ELF_HEADER_SIZE];
        in.readFully(bytes);
        if (bytes[0] != 127 ||
                bytes[1] != 'E' ||
                bytes[2] != 'L' ||
                bytes[3] != 'F' ||
                (bytes[4] != 1 && bytes[4] != 2)) {
            Log.i(TAG, "ELF header invalid");
            return false;
        }

        elf.is64bits = bytes[4] == 2;
        elf.order = bytes[5] == 1
                ? ByteOrder.LITTLE_ENDIAN // ELFDATA2LSB
                : ByteOrder.BIG_ENDIAN;   // ELFDATA2MSB

        // wrap bytes in a ByteBuffer to force endianess
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(elf.order);

        elf.e_machine = buffer.getShort(18);    /* Architecture */
        elf.e_shoff = buffer.getInt(32);        /* Section header table file offset */
        elf.e_shnum = buffer.getShort(48);      /* Section header table entry count */
        return true;
    }

    private static boolean readSection(RandomAccessFile in, ElfData elf) throws IOException {
        byte[] bytes = new byte[SECTION_HEADER_SIZE];
        in.seek(elf.e_shoff);

        for (int i = 0; i < elf.e_shnum; ++i) {
            in.readFully(bytes);

            // wrap bytes in a ByteBuffer to force endianess
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            buffer.order(elf.order);

            int sh_type = buffer.getInt(4); /* Section type */
            if (sh_type != SHT_ARM_ATTRIBUTES) {
                continue;
            }

            elf.sh_offset = buffer.getInt(16);  /* Section file offset */
            elf.sh_size = buffer.getInt(20);    /* Section size in bytes */
            return true;
        }

        return false;
    }

    private static boolean readArmAttributes(RandomAccessFile in, ElfData elf) throws IOException {
        byte[] bytes = new byte[elf.sh_size];
        in.seek(elf.sh_offset);
        in.readFully(bytes);

        // wrap bytes in a ByteBuffer to force endianess
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(elf.order);

        //http://infocenter.arm.com/help/topic/com.arm.doc.ihi0044e/IHI0044E_aaelf.pdf
        //http://infocenter.arm.com/help/topic/com.arm.doc.ihi0045d/IHI0045D_ABI_addenda.pdf
        if (buffer.get() != 'A') // format-version
        {
            return false;
        }

        // sub-sections loop
        while (buffer.remaining() > 0) {
            int start_section = buffer.position();
            int length = buffer.getInt();
            String vendor = getString(buffer);
            if (vendor.equals("aeabi")) {
                // tags loop
                while (buffer.position() < start_section + length) {
                    int start = buffer.position();
                    int tag = buffer.get();
                    int size = buffer.getInt();
                    // skip if not Tag_File, we don't care about others
                    if (tag != 1) {
                        buffer.position(start + size);
                        continue;
                    }

                    // attributes loop
                    while (buffer.position() < start + size) {
                        tag = getUleb128(buffer);
                        if (tag == 6) { // CPU_arch
                            int arch = getUleb128(buffer);
                            elf.att_arch = CPU_archs[arch];
                        } else if (tag == 27) { // ABI_HardFP_use
                            getUleb128(buffer);
                            elf.att_fpu = true;
                        } else {
                            // string for 4=CPU_raw_name / 5=CPU_name / 32=compatibility
                            // string for >32 && odd tags
                            // uleb128 for other
                            tag %= 128;
                            if (tag == 4 || tag == 5 || tag == 32 || (tag > 32 && (tag & 1) != 0)) {
                                getString(buffer);
                            } else {
                                getUleb128(buffer);
                            }
                        }
                    }
                }
                break;
            }
        }
        return true;
    }

    private static String getString(ByteBuffer buffer) {
        StringBuilder sb = new StringBuilder(buffer.limit());
        while (buffer.remaining() > 0) {
            char c = (char) buffer.get();
            if (c == 0) {
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static int getUleb128(ByteBuffer buffer) {
        int ret = 0;
        int c;
        do {
            ret <<= 7;
            c = buffer.get();
            ret |= c & 0x7f;
        } while ((c & 0x80) > 0);

        return ret;
    }

    private static final String URI_AUTHORIZED_CHARS = "'()*";
}
