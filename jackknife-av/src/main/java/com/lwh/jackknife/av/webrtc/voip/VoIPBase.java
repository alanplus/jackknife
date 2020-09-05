/*
 * Copyright (C) 2020 The JackKnife Open Source Project
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

package com.lwh.jackknife.av.webrtc.voip;

import android.content.Context;
import android.util.Log;

import com.lwh.jackknife.av.webrtc.SimpleSdpObserver;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 音视频通话P2P通信基础类，仅提供PeerConnection通信相关处理，在这个类不要处理业务层的内容如刷新UI等。
 */
public abstract class VoIPBase implements IVoIPMode, PeerConnection.Observer, SdpObserver {

    /**
     * 默认视频相关的硬件，内置的一般为v0。
     */
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    /**
     * 默认音频相关的硬件，内置的一般为a0。
     */
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";
    /**
     * 视频编解码器的一种，VP8视频图像编解码器。
     */
    public static final String VIDEO_CODEC_VP8 = "VP8";
    /**
     * 视频编解码器的一种，VP9视频图像编解码器。
     */
    public static final String VIDEO_CODEC_VP9 = "VP9";
    /**
     * 视频编解码器的一种，H.264编解码器，比较主流。
     */
    public static final String VIDEO_CODEC_H264 = "H264";
    /**
     * 音频编解码器的一种，Opus音频编解码器。
     */
    public static final String AUDIO_CODEC_OPUS = "opus";
    /**
     * 音频编解码器的一种，iSAC音频语音编码器。
     */
    public static final String AUDIO_CODEC_ISAC = "ISAC";
    public static final String CONSTRAINT_AUDIO_ECHO_CANCELLATION = "googEchoCancellation";
    public static final String CONSTRAINT_AUDIO_ECHO_CANCELLATION_2 = "googEchoCancellation2";
    public static final String CONSTRAINT_AUDIO_AUTO_GAIN_CONTROL = "googAutoGainControl";
    public static final String CONSTRAINT_AUDIO_AUTO_GAIN_CONTROL_2 = "googAutoGainControl2";
    public static final String CONSTRAINT_AUDIO_HIGH_PASS_FILTER = "googHighpassFilter";
    public static final String CONSTRAINT_AUDIO_NOISE_SUPPRESSION = "googNoiseSuppression";
    public static final String CONSTRAINT_AUDIO_NOISE_SUPPRESSION_2 = "googNoiseSuppression2";
    public static final String CONSTRAINT_AUDIO_LEVEL_CONTROL = "levelControl";
    public static final String CONSTRAINT_DTLS_SRTP_KEY_AGREEMENT = "DtlsSrtpKeyAgreement";
    public static final String CONSTRAINT_AUDIO_MIRRORING = "googAudioMirroring";
    public static final String CONSTRAINT_DA_ECHO_CANCELLATION = "googDAEchoCancellation";
    public static final String CONSTRAINT_TYPING_NOISE_DETECTION = "googTypingNoiseDetection";
    /**
     * 这是一个综合显示效果和网络丢包的推荐视频宽度。
     * 参考：
     * 4K (3840 x 2160)
     * Full HD (1920 x 1080)
     * HD (1280 x 720)
     * VGA (640 x 480)
     * QVGA (320 x 240)
     */
    public static final int HD_VIDEO_WIDTH = 1280;
    /**
     * 这是一个综合显示效果和网络丢包的推荐视频高度。
     * 参考：
     * 4K (3840 x 2160)
     * Full HD (1920 x 1080)
     * HD (1280 x 720)
     * VGA (640 x 480)
     * QVGA (320 x 240)
     */
    public static final int HD_VIDEO_HEIGHT = 720;
    private static final String TAG = "VoIPBase";
    private static final String VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate";
    private static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";
    /**
     * 用于轮询分发消息到所有{@link PeerConnection.Observer}订阅者。
     */
    private final LinkedList<PeerConnection.Observer> mPeerConnectionObservers;
    /**
     * 用于轮询分发消息到所有{@link SdpObserver}订阅者。
     */
    private final LinkedList<SdpObserver> mSdpObservers;
    /**
     * 通过改变这个模式，在其子类的业务层可以达到相同功能的不同表现。
     */
    protected IVoIPMode mMode;
    /**
     * P2P通信的连接对象，PeerConnection to PeerConnection，两端都使用对等连接，可以做到实时音视频。
     */
    protected PeerConnection mPeerConnection;
    /**
     * 用于PeerConnection相关对象的创建。
     */
    protected PeerConnectionFactory mPeerConnectionFactory;
    /**
     * 提供一个线程池用来发送异步的网络请求，如create offer和create answer。
     */
    private ScheduledExecutorService mExecutor;

    /**
     * 音轨集合，属于音频编辑合成的范畴，比如人声、鸟叫声、钢琴、架子鼓分别是一个音轨，一个封装格式的音频文件
     * （如MP3、AAC、FLAC）通常包含多种声音，即多个音轨。
     */
    protected LinkedList<AudioTrack> mAudioTracks;

    /**
     * 视频轨集合，类似于音轨，通常在视频剪辑合成中应用，可以给截取一定长度的视频和添加画中画等。
     */
    protected LinkedList<VideoTrack> mVideoTracks;

    /**
     * 音频源，是一个提供音频媒体数据的地方，比如Android手机系统内置麦克风、外接话筒。
     */
    private AudioSource mAudioSource;

    /**
     * 视频源，是一个提供视频媒体数据的地方，比如Android手机系统前置后置摄像头、屏幕录制。
     */
    private VideoSource mVideoSource;
    /**
     * 媒体约束，将音频和视频放入MediaStream的配置，参考
     * https://www.w3.org/TR/mediacapture-streams/#media-track-supported-constraints。
     */
    private MediaConstraints mMediaConstraints;

    /**
     * 随PeerConnection创建。
     */
    private MediaStream mMediaStream;

    /**
     * 是否创建出对象就自动连接，自动连接则不需要手动在创建对象后调用initPeerConnection()方法。
     */
    private boolean mAutoConnect;

    /**
     * 默认自动创建PeerConnection。
     *
     * @param mode
     */
    public VoIPBase(IVoIPMode mode) {
        this(mode, true);
    }

    /**
     * 可以配置是否自动创建PeerConnection。
     *
     * @param mode
     * @param autoConnect 默认true
     */
    public VoIPBase(IVoIPMode mode, boolean autoConnect) {
        this.mMode = mode;
        this.mAutoConnect = autoConnect;
        this.mExecutor = Executors.newSingleThreadScheduledExecutor();
        this.mPeerConnectionObservers = new LinkedList<>();
        this.mSdpObservers = new LinkedList<>();
        this.mAudioTracks = new LinkedList<>();
        this.mVideoTracks = new LinkedList<>();
        if (autoConnect) {
            this.initPeerConnection(this);
        } else {
            this.initPeerConnection(this, false);
        }
    }

    /**
     * 断开PeerConnection，重置并销毁对象，一次完整的音视频通话流程完成后调用。
     */
    public void stop() {
        mPeerConnection.close();
        mPeerConnection = null;
        mMediaConstraints = null;
        mAutoConnect = false;
        mAudioSource = null;
        mVideoSource = null;
        mAudioTracks.clear();
        mVideoTracks.clear();
        mMediaStream = null;
        mPeerConnectionFactory = null;
        mPeerConnectionObservers.clear();
        mSdpObservers.clear();
    }

    /**
     * 获取对象创建时PeerConnection是否是自动连接的。
     *
     * @return
     */
    public boolean isAutoConnect() {
        return mAutoConnect;
    }

    /**
     * 获取默认的SDP媒体约束。
     *
     * @return
     */
    public MediaConstraints getSdpConstraints() {
        MediaConstraints sdpConstraints = new MediaConstraints();
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        if (mMode.getCurrentModeName().equals("视频通话")) {
            sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        }
        return sdpConstraints;
    }

    /**
     * 添加PeerConnection回调。
     *
     * @param observer
     */
    public void addPeerConnectionObserver(PeerConnection.Observer observer) {
        synchronized (mPeerConnectionObservers) {
            mPeerConnectionObservers.add(observer);
        }
    }

    /**
     * 添加SDP回调。
     *
     * @param observer
     */
    public void addSdpObserver(SdpObserver observer) {
        synchronized (mSdpObservers) {
            mSdpObservers.add(observer);
        }
    }

    /**
     * 获取PeerConnection的媒体约束。
     *
     * @return
     */
    public MediaConstraints getMediaConstraints() {
        return mMediaConstraints;
    }

    public void setAudioSource(MediaConstraints constraints) {
        AudioSource source = mPeerConnectionFactory.createAudioSource(constraints);
        addAudioTrack(source);
    }

    public void setAudioSource(AudioSource source) {
        this.mAudioSource = source;
        addAudioTrack(source);
    }

    public void setVideoSource(VideoCapturer capture) {
        VideoSource source = mPeerConnectionFactory.createVideoSource(capture);
        this.mVideoSource = source;
    }

    public void setVideoSource(VideoSource source) {
        this.mVideoSource = source;
    }

    /**
     * 可指定打洞服务器的RTC配置。
     *
     * @param iceServers
     * @return
     */
    public PeerConnection.RTCConfiguration createRTCConfiguration(List<PeerConnection.IceServer> iceServers) {
        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(iceServers);
        //TCP候选策略控制开关，只有Enable和Disable，TCP策略虽然有握手来保证传输到达率，但这也是效率上最致命的，会极大拉低视频传输效率，所以建议不开启，除非有特别的需求。
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        //协商策略，balanced、max-compat、max-bundle,基本上是选择max-bundle，主要是防止另一个客户端属于策略不可协商型。
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        //实时传输控制协议多路策略，negotiate、require，第一个是获取实时传输控制协议策略和实时传输协议策略，第二个只获取实时传输协议策略，如果另一个客户端不支持实时传输控制协议，那么协商就会失败。Tiki客户端测试发现Require比较适合移动客户端。
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        //收集策略时间段，有GATHER_ONCE和 GATHER_CONTINUALLY两种值，默认值为GATHER_ONCE，可以不用改。
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
//        rtcConfig.iceTransportsType = PeerConnection.IceTransportsType.ALL;
        // Use ECDSA encryption.
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        return rtcConfig;
    }

    /**
     * 创建默认的RTC配置。
     *
     * @return
     */
    private PeerConnection.RTCConfiguration createRTCConfiguration() {
        // 打洞服务器列表，使用STUN和TURN进行NAT内网穿透
        // Interactive Connectivity Establishment, 交互式连接建立. 其实是一个整合STUN和TURN的框架, 给它提
        // 供STUN和TURN服务器地址, 它会自动选择优先级高的进行NAT穿透
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        return createRTCConfiguration(iceServers);
    }

    /**
     * 请参考文档 https://www.w3.org/TR/mediacapture-streams/#media-track-supported-constraints。
     *
     * @return
     */
    private MediaConstraints createPeerConnectionMediaConstraints() {
        MediaConstraints constraints = new MediaConstraints();
        constraints.optional.add(new MediaConstraints.KeyValuePair(CONSTRAINT_DTLS_SRTP_KEY_AGREEMENT, "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair(CONSTRAINT_AUDIO_ECHO_CANCELLATION, "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair(CONSTRAINT_AUDIO_ECHO_CANCELLATION_2, "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair(CONSTRAINT_DA_ECHO_CANCELLATION, "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair(CONSTRAINT_TYPING_NOISE_DETECTION, "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair(CONSTRAINT_AUDIO_AUTO_GAIN_CONTROL, "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair(CONSTRAINT_AUDIO_AUTO_GAIN_CONTROL_2, "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair(CONSTRAINT_AUDIO_NOISE_SUPPRESSION, "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair(CONSTRAINT_AUDIO_NOISE_SUPPRESSION_2, "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair(CONSTRAINT_AUDIO_MIRRORING, "false"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair(CONSTRAINT_AUDIO_HIGH_PASS_FILTER, "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair(CONSTRAINT_AUDIO_LEVEL_CONTROL, "true"));
        return constraints;
    }

    /**
     * 用于手动创建PeerConnection，需要预览一个远端画面的情况。
     *
     * @param observer
     */
    public PeerConnection initPeerConnection(PeerConnection.Observer observer, boolean autoAddStream) {
        if (isAutoConnect()) {
            Log.e(TAG, "不支持自动方式创建PeerConnection");
            return null;
        } else {
            PeerConnection.RTCConfiguration rtcConfig = createRTCConfiguration();
            PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
            MediaConstraints constraints = createPeerConnectionMediaConstraints();
            initPeerConnection(observer, rtcConfig, constraints, options, autoAddStream);
        }
        return mPeerConnection;
    }

    /**
     * 默认的PeerConnection配置，自动连接。
     *
     * @param observer
     */
    private void initPeerConnection(PeerConnection.Observer observer) {
        PeerConnection.RTCConfiguration rtcConfig = createRTCConfiguration();
        MediaConstraints constraints = createPeerConnectionMediaConstraints();
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        initPeerConnection(observer, rtcConfig, constraints, options, true);
    }

    /**
     * 自定义的PeerConnection配置。
     *
     * @param observer
     * @param rtcConfig
     * @param constraints
     * @param options
     */
    public PeerConnection initPeerConnection(PeerConnection.Observer observer, PeerConnection.RTCConfiguration rtcConfig,
                                             MediaConstraints constraints, PeerConnectionFactory.Options options) {
        if (isAutoConnect()) {
            Log.e(TAG, "不支持自动方式创建PeerConnection");
            return null;
        } else {
            initPeerConnection(observer, rtcConfig, constraints, options, false);
        }
        return mPeerConnection;
    }

    /**
     * 最全的PeerConnection配置。
     *
     * @param observer
     * @param rtcConfig
     * @param constraints
     * @param options
     * @param autoAddStream
     */
    private void initPeerConnection(PeerConnection.Observer observer,
                                    PeerConnection.RTCConfiguration rtcConfig,
                                    MediaConstraints constraints,
                                    PeerConnectionFactory.Options options, boolean autoAddStream) {
        mPeerConnectionFactory = createPeerConnectionFactory(options);
        mMediaConstraints = constraints;
        mPeerConnection = createPeerConnection(constraints, rtcConfig, mPeerConnectionFactory,
                observer);
        mMediaStream = createMediaStream();
        if (autoAddStream) {
            mPeerConnection.addStream(mMediaStream);
        }
    }

    /**
     * 创建一个PeerConnection的工厂类。
     *
     * @param options
     * @return
     */
    public PeerConnectionFactory createPeerConnectionFactory(PeerConnectionFactory.Options options) {
        //Params are context, initAudio,initVideo and videoCodecHwAcceleration
        PeerConnectionFactory.initializeAndroidGlobals(mMode.getContext(), true, true, true);
        return new PeerConnectionFactory(options);
    }

    /**
     * 修改SDP的码率信息。
     *
     * @param codec
     * @param isVideoCodec
     * @param sdpDescription
     * @param bitrateKbps
     * @return
     */
    public String setBitrate(
            String codec, boolean isVideoCodec, String sdpDescription, int bitrateKbps) {
        String[] lines = sdpDescription.split("\r\n");
        int rtpmapLineIndex = -1;
        boolean sdpFormatUpdated = false;
        String codecRtpMap = null;
        // Search for codec rtpmap in format
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);
        for (int i = 0; i < lines.length; i++) {
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
                rtpmapLineIndex = i;
                break;
            }
        }
        if (codecRtpMap == null) {
            Log.w(TAG, "No rtpmap for " + codec + " codec");
            return sdpDescription;
        }
        Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + " at " + lines[rtpmapLineIndex]);

        // Check if a=fmtp string already exist in remote SDP for this codec and
        // update it with new bitrate parameter.
        regex = "^a=fmtp:" + codecRtpMap + " \\w+=\\d+.*[\r]?$";
        codecPattern = Pattern.compile(regex);
        for (int i = 0; i < lines.length; i++) {
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                Log.d(TAG, "Found " + codec + " " + lines[i]);
                if (isVideoCodec) {
                    lines[i] += "; " + VIDEO_CODEC_PARAM_START_BITRATE + "=" + bitrateKbps;
                } else {
                    lines[i] += "; " + AUDIO_CODEC_PARAM_BITRATE + "=" + (bitrateKbps * 1000);
                }
                Log.d(TAG, "Update remote SDP line: " + lines[i]);
                sdpFormatUpdated = true;
                break;
            }
        }

        StringBuilder newSdpDescription = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            newSdpDescription.append(lines[i]).append("\r\n");
            // Append new a=fmtp line if no such line exist for a codec.
            if (!sdpFormatUpdated && i == rtpmapLineIndex) {
                String bitrateSet;
                if (isVideoCodec) {
                    bitrateSet =
                            "a=fmtp:" + codecRtpMap + " " + VIDEO_CODEC_PARAM_START_BITRATE + "=" + bitrateKbps;
                } else {
                    bitrateSet = "a=fmtp:" + codecRtpMap + " " + AUDIO_CODEC_PARAM_BITRATE + "="
                            + (bitrateKbps * 1000);
                }
                Log.d(TAG, "Add remote SDP line: " + bitrateSet);
                newSdpDescription.append(bitrateSet).append("\r\n");
            }
        }
        return newSdpDescription.toString();
    }

    /**
     * 修改SDP的音视频编解码器信息。
     *
     * @param sdpDescription
     * @param codec
     * @param isAudio
     * @return
     */
    public String preferCodec(String sdpDescription, String codec, boolean isAudio) {
        String[] lines = sdpDescription.split("\r\n");
        int lineIndex = -1;
        String codecRtpMap = null;
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);
        String mediaDescription = "m=video ";
        if (isAudio) {
            mediaDescription = "m=audio ";
        }
        for (int i = 0; (i < lines.length) && (lineIndex == -1 || codecRtpMap == null); i++) {
            if (lines[i].startsWith(mediaDescription)) {
                lineIndex = i;
                continue;
            }
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
            }
        }
        if (lineIndex == -1) {
            Log.w(TAG, "No " + mediaDescription + " line, so can't prefer " + codec);
            return sdpDescription;
        }
        if (codecRtpMap == null) {
            Log.w(TAG, "No rtpmap for " + codec);
            return sdpDescription;
        }
        Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + ", prefer at " + lines[lineIndex]);
        String[] origMLineParts = lines[lineIndex].split(" ");
        if (origMLineParts.length > 3) {
            StringBuilder newMLine = new StringBuilder();
            int origPartIndex = 0;
            // Format is: m=<media> <port> <proto> <fmt> ...
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(codecRtpMap);
            for (; origPartIndex < origMLineParts.length; origPartIndex++) {
                if (!origMLineParts[origPartIndex].equals(codecRtpMap)) {
                    newMLine.append(" ").append(origMLineParts[origPartIndex]);
                }
            }
            lines[lineIndex] = newMLine.toString();
            Log.d(TAG, "Change media description: " + lines[lineIndex]);
        } else {
            Log.e(TAG, "Wrong SDP media description format: " + lines[lineIndex]);
        }
        StringBuilder newSdpDescription = new StringBuilder();
        for (String line : lines) {
            newSdpDescription.append(line).append("\r\n");
        }
        return newSdpDescription.toString();
    }

    /**
     * 创建媒体流。
     */
    public MediaStream createMediaStream() {
        if (mPeerConnection != null) {
            return mPeerConnectionFactory.createLocalMediaStream("ARDAMS");
        }
        return null;
    }

    /**
     * 创建音轨。
     *
     * @return
     */
    private AudioTrack createAudioTrack(AudioSource source) {
        AudioTrack audioTrack = mPeerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, source);
        audioTrack.setEnabled(true);
        return audioTrack;
    }

    /**
     * 给PeerConnection的媒体流添加音轨。
     */
    void addAudioTrack(AudioSource source) {
        AudioTrack audioTrack = createAudioTrack(source);
        mMediaStream.addTrack(audioTrack);
        audioTrack.setEnabled(true);
        mAudioTracks.add(audioTrack);
    }

    /**
     * 创建视频轨。
     *
     * @param callbacks
     * @return
     */
    private VideoTrack createVideoTrack(VideoRenderer.Callbacks callbacks, VideoSource source) {
        VideoTrack videoTrack = mPeerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, source);
        videoTrack.setEnabled(true);
        videoTrack.addRenderer(new VideoRenderer(callbacks));
        return videoTrack;
    }

    /**
     * 给PeerConnection的媒体流添加视频轨。
     */
    void addVideoTrack(VideoRenderer.Callbacks callbacks, VideoSource source) {
        VideoTrack videoTrack = createVideoTrack(callbacks, source);
        mMediaStream.addTrack(videoTrack);
        mVideoTracks.add(videoTrack);
    }

    public void addVideoTrack(VideoRenderer.Callbacks callbacks) {
        addVideoTrack(callbacks, mVideoSource);
    }

    public void setVideoSource(VideoCapturer capture, VideoRenderer.Callbacks callbacks) {
        setVideoSource(capture);
        addVideoTrack(callbacks);
    }

    /**
     * 这个方法决定PeerConnection怎么创建，可定制性最高。
     *
     * @return
     */
    public PeerConnection createPeerConnection(MediaConstraints constraints,
                                               PeerConnection.RTCConfiguration rtcConfig,
                                               PeerConnectionFactory factory,
                                               PeerConnection.Observer observer) {
        return factory.createPeerConnection(rtcConfig, constraints, observer);
    }

    /**
     * 获取当前的模式。
     *
     * @return
     */
    public IVoIPMode getMode() {
        return mMode;
    }

    /**
     * 修改模式。
     *
     * @param mode
     */
    public void setMode(IVoIPMode mode) {
        this.mMode = mode;
    }

    /**
     * 获取具体的模式对象。
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends IVoIPMode> T getMode(Class<T> clazz) {
        return (T) mMode;
    }

    /**
     * 创建视频录制，捕捉画面，这里仅用于预览。
     *
     * @param context
     * @param videoWidth
     * @param videoHeight
     * @param fps
     * @return
     */
    public VideoCapturer createVideoCapture(Context context, int videoWidth, int videoHeight, int fps) {
        VideoCapturer videoCapturer;
        boolean useCamera2 = Camera2Enumerator.isSupported(context);
        if (useCamera2) {
            videoCapturer = createCameraCapture(new Camera2Enumerator(context));
        } else {
            videoCapturer = createCameraCapture(new Camera1Enumerator(true));
        }
        videoCapturer.startCapture(videoWidth, videoHeight, fps);
        return videoCapturer;
    }

    private VideoCapturer createCameraCapture(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

    /**
     * 获取对等连接。
     *
     * @return
     */
    public PeerConnection getPeerConnection() {
        return mPeerConnection;
    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        if (mPeerConnectionObservers.size() > 0) {
            synchronized (mPeerConnectionObservers) {
                for (PeerConnection.Observer observer : mPeerConnectionObservers) {
                    observer.onSignalingChange(signalingState);
                }
            }
        }
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        if (mPeerConnectionObservers.size() > 0) {
            synchronized (mPeerConnectionObservers) {
                for (PeerConnection.Observer observer : mPeerConnectionObservers) {
                    observer.onIceConnectionChange(iceConnectionState);
                }
            }
        }
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        if (mPeerConnectionObservers.size() > 0) {
            synchronized (mPeerConnectionObservers) {
                for (PeerConnection.Observer observer : mPeerConnectionObservers) {
                    observer.onIceConnectionReceivingChange(b);
                }
            }
        }
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        if (mPeerConnectionObservers.size() > 0) {
            synchronized (mPeerConnectionObservers) {
                for (PeerConnection.Observer observer : mPeerConnectionObservers) {
                    observer.onIceGatheringChange(iceGatheringState);
                }
            }
        }
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        if (mPeerConnectionObservers.size() > 0) {
            synchronized (mPeerConnectionObservers) {
                for (PeerConnection.Observer observer : mPeerConnectionObservers) {
                    observer.onIceCandidate(iceCandidate);
                }
            }
        }
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        if (mPeerConnectionObservers.size() > 0) {
            synchronized (mPeerConnectionObservers) {
                for (PeerConnection.Observer observer : mPeerConnectionObservers) {
                    observer.onIceCandidatesRemoved(iceCandidates);
                }
            }
        }
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        if (mPeerConnectionObservers.size() > 0) {
            synchronized (mPeerConnectionObservers) {
                for (PeerConnection.Observer observer : mPeerConnectionObservers) {
                    observer.onAddStream(mediaStream);
                }
            }
        }
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        if (mPeerConnectionObservers.size() > 0) {
            synchronized (mPeerConnectionObservers) {
                for (PeerConnection.Observer observer : mPeerConnectionObservers) {
                    observer.onRemoveStream(mediaStream);
                }
            }
        }
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        if (mPeerConnectionObservers.size() > 0) {
            synchronized (mPeerConnectionObservers) {
                for (PeerConnection.Observer observer : mPeerConnectionObservers) {
                    observer.onDataChannel(dataChannel);
                }
            }
        }
    }

    @Override
    public void onRenegotiationNeeded() {
        if (mPeerConnectionObservers.size() > 0) {
            synchronized (mPeerConnectionObservers) {
                for (PeerConnection.Observer observer : mPeerConnectionObservers) {
                    observer.onRenegotiationNeeded();
                }
            }
        }
    }

    /**
     * 获取媒体流。
     *
     * @return
     */
    public MediaStream getMediaStream() {
        return mMediaStream;
    }

    /**
     * 获取音源。
     *
     * @return
     */
    public AudioSource getAudioSource() {
        return mAudioSource;
    }

    /**
     * 获取视频源。
     *
     * @return
     */
    public VideoSource getVideoSource() {
        return mVideoSource;
    }

    /**
     * 获取所有的音轨，一般只有一个。
     *
     * @return
     */
    public LinkedList<AudioTrack> getAudioTracks() {
        return mAudioTracks;
    }

    /**
     * 获取所有的视频轨，一般只有一个。
     *
     * @return
     */
    public LinkedList<VideoTrack> getVideoTracks() {
        return mVideoTracks;
    }

    /**
     * 获取PeerConnection的工厂类。
     *
     * @return
     */
    public PeerConnectionFactory getPeerConnectionFactory() {
        return mPeerConnectionFactory;
    }

    /**
     * 获取默认的线程池执行器。
     *
     * @return
     */
    public ScheduledExecutorService getExecutor() {
        return mExecutor;
    }

    /**
     * 自定义保存remote answer sdp。
     *
     * @param remoteSdp
     * @param observer
     */
    public void saveRemoteAnswerSdp(SessionDescription remoteSdp, SimpleSdpObserver observer) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (mPeerConnection != null) {
                    mPeerConnection.setRemoteDescription(observer, remoteSdp);
                }
            }
        });
    }

    /**
     * 保存remote answer sdp封装。
     *
     * @param remoteSdp
     */
    public void saveRemoteAnswerSdp(SessionDescription remoteSdp) {
        saveRemoteAnswerSdp(remoteSdp, new SimpleSdpObserver());
    }

    /**
     * 自定义保存remote offer sdp。
     *
     * @param remoteSdp
     * @param observer
     */
    public void saveRemoteOfferSdp(SessionDescription remoteSdp, SimpleSdpObserver observer) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (mPeerConnection != null) {
                    mPeerConnection.setRemoteDescription(observer, remoteSdp);
                }
            }
        });
    }

    /**
     * 保存remote offer sdp封装。
     *
     * @param remoteSdp
     */
    public void saveRemoteOfferSdp(SessionDescription remoteSdp) {
        saveRemoteOfferSdp(remoteSdp, new SimpleSdpObserver() {
            @Override
            public void onSetSuccess() {
                createAnswer();
            }
        });
    }

    /**
     * 自定义保存local sdp。
     *
     * @param localSdp
     * @param observer
     */
    public void saveLocalSdp(SessionDescription localSdp, SimpleSdpObserver observer) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (mPeerConnection != null) {
                    mPeerConnection.setLocalDescription(observer, localSdp);
                }
            }
        });
    }

    /**
     * 保存local sdp封装。
     *
     * @param localSdp
     */
    public void saveLocalSdp(SessionDescription localSdp) {
        saveLocalSdp(localSdp, new SimpleSdpObserver());
    }

    /**
     * 自定义创建offer。
     *
     * @param observer
     */
    public void createOffer(SimpleSdpObserver observer) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mPeerConnection.createOffer(observer, getSdpConstraints());
            }
        });
    }

    /**
     * 发起方创建offer封装。
     */
    public void createOffer() {
        createOffer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sdp) {
                if (sdp.type == SessionDescription.Type.OFFER) {
                    saveLocalSdp(sdp);
                    sendSessionDescription(true, sdp);
                }
            }
        });
    }

    /**
     * 自定义创建answer。
     *
     * @param observer
     */
    public void createAnswer(SimpleSdpObserver observer) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mPeerConnection.createAnswer(observer, getSdpConstraints());
            }
        });
    }

    /**
     * 接听方创建answer封装。
     */
    public void createAnswer() {
        createAnswer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sdp) {
                if (sdp.type == SessionDescription.Type.ANSWER) {
                    saveLocalSdp(sdp);
                }
            }
        });
    }

    /**
     * 给PeerConnection添加ICE令牌。
     *
     * @param iceCandidate
     */
    public void addIceCandidate(IceCandidate iceCandidate) {
        if (mPeerConnection != null) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mPeerConnection.addIceCandidate(iceCandidate);
                }
            });
        }
    }

    public void removeIceCandidates(IceCandidate[] iceCandidates) {
        if (mPeerConnection != null) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mPeerConnection.removeIceCandidates(iceCandidates);
                }
            });
        }
    }

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        if (mSdpObservers.size() > 0) {
            synchronized (mSdpObservers) {
                for (SdpObserver observer : mSdpObservers) {
                    observer.onCreateSuccess(sessionDescription);
                }
            }
        }
    }

    @Override
    public void onSetSuccess() {
        if (mSdpObservers.size() > 0) {
            synchronized (mSdpObservers) {
                for (SdpObserver observer : mSdpObservers) {
                    observer.onSetSuccess();
                }
            }
        }
    }

    @Override
    public void onCreateFailure(String s) {
        if (mSdpObservers.size() > 0) {
            synchronized (mSdpObservers) {
                for (SdpObserver observer : mSdpObservers) {
                    observer.onCreateFailure(s);
                }
            }
        }
    }

    @Override
    public void onSetFailure(String s) {
        if (mSdpObservers.size() > 0) {
            synchronized (mSdpObservers) {
                for (SdpObserver observer : mSdpObservers) {
                    observer.onSetFailure(s);
                }
            }
        }
    }
}
