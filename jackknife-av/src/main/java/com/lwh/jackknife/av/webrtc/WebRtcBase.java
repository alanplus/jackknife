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

package com.lwh.jackknife.av.webrtc;

import android.content.Context;
import android.util.Log;

import com.lwh.jackknife.av.webrtc.mode.IWebRtcMode;

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
 * 音视频通话通信基础类，仅提供对等连接通信，封装PeerConnection，在这个类不要写具体的业务和刷新UI等。
 */
public abstract class WebRtcBase implements IWebRtcMode, PeerConnection.Observer {

    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";
    public static final String VIDEO_TRACK_TYPE = "video";
    public static final String VIDEO_CODEC_VP8 = "VP8";
    public static final String VIDEO_CODEC_VP9 = "VP9";
    public static final String VIDEO_CODEC_H264 = "H264";
    public static final String AUDIO_CODEC_OPUS = "opus";
    public static final String AUDIO_CODEC_ISAC = "ISAC";
    public static final String VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate";
    public static final String VIDEO_FLEXFEC_FIELDTRIAL = "WebRTC-FlexFEC-03/Enabled/";
    public static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";
    public static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    public static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    public static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    public static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
    public static final String AUDIO_LEVEL_CONTROL_CONSTRAINT = "levelControl";
    public static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";
    public static final int HD_VIDEO_WIDTH = 1280;
    public static final int HD_VIDEO_HEIGHT = 720;
    private static final String TAG = "WebRtcMode";
    private final LinkedList<PeerConnection.Observer> mPeerConnectionObservers;
    private final LinkedList<SdpObserver> mSdpObservers;
    protected IWebRtcMode mMode;
    /**
     * P2P通信的连接对象，PeerConnection to PeerConnection，两端都使用对等连接，可以做到实时音视频。
     */
    protected PeerConnection mPeerConnection;
    /**
     * 音轨集合，属于音频编辑合成的范畴，比如人声、鸟叫声、钢琴、架子鼓分别是一个音轨，一个封装格式的音频文件
     * （如MP3、AAC、FLAC）通常包含多种声音，即多个音轨。
     */
    protected LinkedList<AudioTrack> mAudioTracks;
    /**
     * 视频轨集合，类似于音轨，通常在视频剪辑合成中应用，可以给截取一定长度的视频和添加画中画等。
     */
    protected LinkedList<VideoTrack> mVideoTracks;
    private ScheduledExecutorService mExecutor;
    /**
     * 随PeerConnection创建。
     */
    private MediaStream mMediaStream;
    /**
     * 音频源，是一个提供音频媒体数据的地方，比如Android手机系统内置麦克风、外接话筒。
     */
    private AudioSource mAudioSource;

    /**
     * 视频源，是一个提供视频媒体数据的地方，比如Android手机系统前置后置摄像头、屏幕录制。
     */
    private VideoSource mVideoSource;

    /**
     * 媒体约束，将音频和视频放入MediaStream的配置。
     */
    private MediaConstraints mMediaConstraints;

    /**
     * 用于PeerConnection相关对象的创建。
     */
    private PeerConnectionFactory mPeerConnectionFactory;

    /**
     * 是否创建出对象就自动连接，自动连接则不需要手动在创建对象后调用initPeerConnection()方法。
     */
    private boolean mAutoConnect;

    public WebRtcBase(IWebRtcMode mode) {
        this(mode, false);
    }

    public WebRtcBase(IWebRtcMode mode, boolean autoConnect) {
        this.mMode = mode;
        this.mAutoConnect = autoConnect;
        this.mExecutor = Executors.newSingleThreadScheduledExecutor();
        this.mPeerConnectionObservers = new LinkedList<>();
        this.mSdpObservers = new LinkedList<>();
        this.mAudioTracks = new LinkedList<>();
        this.mVideoTracks = new LinkedList<>();
        if (autoConnect) {
            this.initPeerConnection(this);
        }
    }

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

    public boolean isAutoConnect() {
        return mAutoConnect;
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

    public MediaConstraints getMediaConstraints() {
        return mMediaConstraints;
    }

    public void setMediaConstraints(MediaConstraints constraints) {
        this.mMediaConstraints = constraints;
    }

    /**
     * 使用默认的PeerConnection配置。
     *
     * @param observer
     */
    private void initPeerConnection(PeerConnection.Observer observer) {
        // 打洞服务器列表，使用STUN和TURN进行NAT内网穿透
        // Interactive Connectivity Establishment, 交互式连接建立. 其实是一个整合STUN和TURN的框架, 给它提
        // 供STUN和TURN服务器地址, 它会自动选择优先级高的进行NAT穿透
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(iceServers);
        MediaConstraints constraints = new MediaConstraints();
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        initPeerConnection(observer, rtcConfig, constraints, options);
    }

    /**
     * 完整的PeerConnection配置。
     *
     * @param observer
     * @param rtcConfig
     * @param constraints
     * @param options
     */
    public void initPeerConnection(PeerConnection.Observer observer, PeerConnection.RTCConfiguration rtcConfig,
                                   MediaConstraints constraints, PeerConnectionFactory.Options options) {
        //Params are context, initAudio,initVideo and videoCodecHwAcceleration
        PeerConnectionFactory.initializeAndroidGlobals(mMode.getContext(), true, true, true);
        mPeerConnectionFactory = new PeerConnectionFactory(options);
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        // Use ECDSA encryption.
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        mMediaConstraints = constraints;
        mPeerConnection = createPeerConnection(mMediaConstraints, rtcConfig, mPeerConnectionFactory,
                observer);
        mMediaStream = createMediaStream(mPeerConnectionFactory);
    }

    public String preferCodec(String sdpDescription, String codec, boolean isAudio) {
        String[] lines = sdpDescription.split("\r\n");
        int mLineIndex = -1;
        String codecRtpMap = null;
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);
        String mediaDescription = "m=video ";
        if (isAudio) {
            mediaDescription = "m=audio ";
        }
        for (int i = 0; (i < lines.length) && (mLineIndex == -1 || codecRtpMap == null); i++) {
            if (lines[i].startsWith(mediaDescription)) {
                mLineIndex = i;
                continue;
            }
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
            }
        }
        if (mLineIndex == -1) {
            Log.w(TAG, "No " + mediaDescription + " line, so can't prefer " + codec);
            return sdpDescription;
        }
        if (codecRtpMap == null) {
            Log.w(TAG, "No rtpmap for " + codec);
            return sdpDescription;
        }
        Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + ", prefer at " + lines[mLineIndex]);
        String[] origMLineParts = lines[mLineIndex].split(" ");
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
            lines[mLineIndex] = newMLine.toString();
            Log.d(TAG, "Change media description: " + lines[mLineIndex]);
        } else {
            Log.e(TAG, "Wrong SDP media description format: " + lines[mLineIndex]);
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
    public MediaStream createMediaStream(PeerConnectionFactory factory) {
        if (mPeerConnection != null) {
            return factory.createLocalMediaStream("ARDAMS");
        }
        return null;
    }

    /**
     * 创建音轨。
     *
     * @param factory
     * @return
     */
    private AudioTrack createAudioTrack(AudioSource source, PeerConnectionFactory factory) {
        AudioTrack audioTrack = factory.createAudioTrack(AUDIO_TRACK_ID, source);
        audioTrack.setEnabled(true);
        return audioTrack;
    }

    /**
     * 给PeerConnection的媒体流添加音轨。
     */
    public void addAudioTrack(PeerConnectionFactory factory) {
        AudioTrack audioTrack = createAudioTrack(mAudioSource, factory);
        mMediaStream.addTrack(audioTrack);
        audioTrack.setEnabled(true);
        mAudioTracks.add(audioTrack);
    }

    /**
     * 创建视频轨。
     *
     * @param factory
     * @param callbacks
     * @return
     */
    private VideoTrack createVideoTrack(PeerConnectionFactory factory, VideoRenderer.Callbacks callbacks) {
        VideoTrack videoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, mVideoSource);
        videoTrack.setEnabled(true);
        videoTrack.addRenderer(new VideoRenderer(callbacks));
        return videoTrack;
    }

    /**
     * 给PeerConnection的媒体流添加视频轨。
     */
    public void addVideoTrack(PeerConnectionFactory factory, VideoRenderer.Callbacks callbacks) {
        VideoTrack videoTrack = createVideoTrack(factory, callbacks);
        mMediaStream.addTrack(videoTrack);
        mVideoTracks.add(videoTrack);
    }

    /**
     * 这个方法决定PeerConnection怎么创建。
     *
     * @return
     */
    public PeerConnection createPeerConnection(MediaConstraints constraints,
                                               PeerConnection.RTCConfiguration rtcConfig,
                                               PeerConnectionFactory factory,
                                               PeerConnection.Observer observer) {
        return factory.createPeerConnection(rtcConfig, constraints, observer);
    }

    public IWebRtcMode getMode() {
        return mMode;
    }

    public void setMode(IWebRtcMode mode) {
        this.mMode = mode;
    }

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

    public MediaStream getMediaStream() {
        return mMediaStream;
    }

    public AudioSource getAudioSource() {
        return mAudioSource;
    }

    public void setAudioSource(AudioSource source) {
        this.mAudioSource = source;
    }

    public VideoSource getVideoSource() {
        return mVideoSource;
    }

    public void setVideoSource(VideoSource source) {
        this.mVideoSource = source;
    }

    public LinkedList<AudioTrack> getAudioTracks() {
        return mAudioTracks;
    }

    public LinkedList<VideoTrack> getVideoTracks() {
        return mVideoTracks;
    }

    public PeerConnectionFactory getPeerConnectionFactory() {
        return mPeerConnectionFactory;
    }

    public ScheduledExecutorService getExecutor() {
        return mExecutor;
    }
}
