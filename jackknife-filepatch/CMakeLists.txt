cmake_minimum_required(VERSION 3.4.1)

find_library(
    log-lib
    log
)

add_library(
    jknfpatch
    SHARED
    src/main/cpp/bspatch.c
    src/main/cpp/bzip2/blocksort.c
    src/main/cpp/bzip2/bzip2.c
    src/main/cpp/bzip2/bzip2recover.c
    src/main/cpp/bzip2/bzlib.c
    src/main/cpp/bzip2/compress.c
    src/main/cpp/bzip2/crctable.c
    src/main/cpp/bzip2/decompress.c
    src/main/cpp/bzip2/dlltest.c
    src/main/cpp/bzip2/huffman.c
    src/main/cpp/bzip2/mk251.c
    src/main/cpp/bzip2/randtable.c
    src/main/cpp/bzip2/spewG.c
    src/main/cpp/bzip2/unzcrash.c
)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

include_directories(src/main/cpp/bzip2)

target_link_libraries(
    jknfpatch
    ${log-lib}
)