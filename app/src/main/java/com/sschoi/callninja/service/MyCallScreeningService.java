package com.sschoi.callninja.service;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telecom.CallScreeningService;

import androidx.core.app.NotificationCompat;

import com.sschoi.callninja.R;
import com.sschoi.callninja.data.db.BlockLogDBHelper;
import com.sschoi.callninja.util.ContactHelper;

/**
 * KnownCalls 스타일 화이트리스트 + 차단 로그 서비스
 * - 연락처 / 즐겨찾기 번호만 수신
 * - 나머지 번호 → 벨 울리지 않고 안내멘트 나오게 차단
 * - 차단 로그 DB 저장
 * - 차단 알림 + 진동
 */
public class MyCallScreeningService extends CallScreeningService {

	private static final String CHANNEL_ID = "blocked_calls_channel";

	@Override
	public void onScreenCall(Call.Details callDetails) {
		if (callDetails.getHandle() == null) return;

		String incomingNumber = callDetails.getHandle().getSchemeSpecificPart();

		// 화이트리스트 체크: 연락처 또는 즐겨찾기
		boolean allowCall = ContactHelper.isContact(this, incomingNumber)
				|| ContactHelper.isFavorite(this, incomingNumber);

		if (!allowCall) {
			// 1️⃣ 전화 차단 (벨 울리지 않음, 안내멘트 발생)
			CallResponse response = new CallResponse.Builder()
					.setDisallowCall(true)
					.setRejectCall(true)
					.setSilenceCall(true)
					.build();
			respondToCall(callDetails, response);

			// 2️⃣ 차단 로그 DB 저장
			BlockLogDBHelper db = new BlockLogDBHelper(this);
			db.insertBlockLog(incomingNumber);

			// 3️⃣ 진동
			vibratePhone();

			// 4️⃣ 알림
			showBlockedNotification(incomingNumber);
		}
	}

	/**
	 * 진동 표시
	 */
	private void vibratePhone() {
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if (vibrator != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
			} else {
				vibrator.vibrate(300);
			}
		}
	}

	/**
	 * 차단 알림 표시
	 */
	private void showBlockedNotification(String number) {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if (manager == null) return;

		// Android O 이상 채널 생성
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationCompat.Builder temp = new NotificationCompat.Builder(this, CHANNEL_ID);
			if (manager.getNotificationChannel(CHANNEL_ID) == null) {
				manager.createNotificationChannel(new android.app.NotificationChannel(
						CHANNEL_ID,
						"Blocked Calls",
						NotificationManager.IMPORTANCE_HIGH
				));
			}
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_call_block)
				.setContentTitle("Blocked Call")
				.setContentText(number + " has been blocked")
				.setAutoCancel(true)
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

		manager.notify(number.hashCode(), builder.build());
	}
}
