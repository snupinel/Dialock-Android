package com.example.dailysummary.overlay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.WindowManager
import android.widget.Toast

class OverlayReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // 오버레이 표시
        val overlayIntent = Intent(context, MyService::class.java)
        context.startService(overlayIntent)

        // 확인용 메시지
        //Toast.makeText(context, "오버레이가 표시됩니다.", Toast.LENGTH_SHORT).show()
    }
}
