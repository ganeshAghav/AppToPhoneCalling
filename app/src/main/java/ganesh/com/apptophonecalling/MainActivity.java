package ganesh.com.apptophonecalling;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Call call;
    Button callButton;
    TextView callState;
    EditText phoneNumber;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumber= (EditText) findViewById(R.id.phoneNumber);

        final SinchClient sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId("Any_user_Id")
                .applicationKey("d5638826-81fa-45e9-92ab-7552b88629ab")
                .applicationSecret("pUYeV5YBVk+GHsb36IGiDQ==")
                .environmentHost("sandbox.sinch.com")
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.start();

        callState = (TextView) findViewById(R.id.callState);
        callButton = (Button) findViewById(R.id.callButton);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (call == null) {
                    number=phoneNumber.getText().toString().trim();
                    call = sinchClient.getCallClient().callPhoneNumber(number);
                    call.addCallListener(new SinchCallListener());
                    callButton.setText("Hang Up");
                } else {
                    call.hangup();
                }
            }
        });
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            callButton.setText("Call");
            callState.setText("");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            callState.setText("connected: "+number);
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            callState.setText("Ringing: "+number);
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {}
    }

}
