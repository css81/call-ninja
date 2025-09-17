package com.sschoi.callninja.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telecom.Call;
import android.telecom.CallScreeningService;

import androidx.core.app.NotificationCompat;

import com.sschoi.callninja.R;
import com.sschoi.callninja.data.db.BlockLogDBHelper;
import com.sschoi.callninja.util.ContactHelper;

public class MyCallScreeningService extends CallScreeningService {

    private static final String CHANNEL_ID = "blocked_calls_channel";

    @Override
    public void onScreenCall(Call.Details callDetails) {
        String incomingNumber = callDetails.getHandle().getSchemeSpecificPart();

        if (shouldBlockNumber(incomingNumber)) {
            // 1. 전화 차단
            CallResponse response = new CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSilenceCall(true)
                    .build();
            respondToCall(callDetails, response);

            // 2. 진동
            vibratePhone();

            // 3. 차단 알림
            showBlockedNotification(incomingNumber);

            // 4. DB에 로그 저장
            BlockLogDBHelper db = new BlockLogDBHelper(this);
            db.insertBlockLog(incomingNumber);
        }
    }

	private boolean shouldBlockNumber(String number) {
		// DB 차단 여부
		BlockLogDBHelper db = new BlockLogDBHelper(this);
		boolean isBlockedInDB = db.isNumberBlocked(number);

		// 사용자 수신 정책 확인
		SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
		int policy = prefs.getInt("call_policy", 0); // 기본: ALL

		switch (policy) {
			case 0: // 모든 번호 수신 → 절대 차단하지 않음
				return false;

			case 1: // 연락처만 수신 → 연락처가 아니거나 DB 등록 번호는 차단
				return !ContactHelper.isContact(this, number) || isBlockedInDB;

			case 2: // 즐겨찾기만 수신 → 즐겨찾기가 아니거나 DB 등록 번호 차단
				return !ContactHelper.isFavorite(this, number) || isBlockedInDB;

			case 3: // 연락처 + 즐겨찾기 수신 → 둘 다 아니거나 DB 등록 번호 차단
				return !(ContactHelper.isContact(this, number) || ContactHelper.isFavorite(this, number)) || isBlockedInDB;

			default:
				return isBlockedInDB;
		}
	}



	private void vibratePhone() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        }
    }

	private void showBlockedNotification(String number) {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// 차단된 전화 배지 카운트 가져오기 (SharedPreferences 사용)
		int badgeCount = getBadgeCount();
		badgeCount++; // 새 차단 발생 시 1 증가
		saveBadgeCount(badgeCount);

		// Android O 이상 채널 생성
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(
					CHANNEL_ID,
					"차단된 전화 알림",
					NotificationManager.IMPORTANCE_HIGH
			);
			channel.setDescription("차단된 전화가 발생하면 표시됩니다");
			manager.createNotificationChannel(channel);
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_call_block)
				.setContentTitle("차단된 전화")
				.setContentText(number + " 번호가 차단되었습니다")
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setAutoCancel(true)
				.setNumber(badgeCount) // 배지 숫자 설정
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

		manager.notify(1000, builder.build()); // ID 고정 또는 System.currentTimeMillis() 가능
	}

	private int getBadgeCount() {
		return getSharedPreferences("prefs", MODE_PRIVATE).getInt("badge_count", 0);
	}

	private void saveBadgeCount(int count) {
		getSharedPreferences("prefs", MODE_PRIVATE).edit().putInt("badge_count", count).apply();
	}

}
