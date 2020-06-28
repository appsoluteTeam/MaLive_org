package com.abbsolute.ma_livu.Login;

import android.app.Application;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

public class GlobalApplication extends Application {
    private static GlobalApplication instance;

    public static GlobalApplication getGlobalApplicationContext() {
        if (instance == null) {
            throw new IllegalStateException("This Application does not inherit com.kakao.GlobalApplication");
        }

        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Kakao Sdk 초기화
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }

    public class KakaoSDKAdapter extends KakaoAdapter {

        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {

                // 로그인 시 인증 타입 지정
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
                }

                // pause와 resume시에 타이머를 설정/ CPU의 소모를 절약 할 지의 여부를 지정
                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }


                // Kakao와 제휴 된 앱에서 사용되는 값
                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                // 로그인 웹뷰에서 email 입력 폼의 데이터를 저장할 지 여부를 지정
                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        // Application이 가지고 있는 정보를 얻기 위한 인터페이스
        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return GlobalApplication.getGlobalApplicationContext();
                }
            };
        }
    }
}
