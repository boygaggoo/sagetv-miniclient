package sagex.miniclient.android;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sagex.miniclient.ServerInfo;
import sagex.miniclient.prefs.PrefStore;

/**
 * Created by seans on 22/11/15.
 */
public class AutoConnectDialog extends DialogFragment {
    static final Logger log = LoggerFactory.getLogger(AutoConnectDialog.class);

    @Bind(R.id.text)
    TextView connectText = null;

    int countdownSecs = 5;
    boolean autoConnect = true;
    CountDownTimer countDownTimer;
    boolean connected = false;
    ServerInfo serverInfo;

    public AutoConnectDialog() {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MaterialBaseTheme_Light_Dialog);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MaterialBaseTheme_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_autoconnect, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    private void processOnFinish() {
        log.debug("Finished");
        if (autoConnect) {
            connect();
        } else {
            try {
                dismiss();
            } catch (Throwable t) {
            }
        }
    }

    private synchronized void connect() {
        if (connected) {
            dismiss();
            return;
        }
        connected = true;
        dismiss();
        if (serverInfo != null) {
            ServersActivity.connect(getActivity(), serverInfo);
        }
    }

    @OnClick(R.id.ok)
    public void onOK() {
        countDownTimer.cancel();
        connect();
    }

    @OnClick(R.id.cancel)
    public void onCancel() {
        autoConnect = false;
        countDownTimer.cancel();
        dismiss();
    }

    private void updateText() {
        connectText.setText(getResources().getString(R.string.msg_auto_connect, getTimeLeft()));
    }

    private int getTimeLeft() {
        return countdownSecs;
    }

    @Override
    public void onPause() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        autoConnect = true;
        countdownSecs = MiniclientApplication.get().getClient().properties().getInt(PrefStore.Keys.auto_connect_delay, 10);
        serverInfo = MiniclientApplication.get().getClient().getServers().getLastConnectedServer();
        updateText();

        super.onResume();

        countDownTimer = new CountDownTimer(countdownSecs * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownSecs = (int) (millisUntilFinished / 1000);
                updateText();
            }

            @Override
            public void onFinish() {
                processOnFinish();
            }
        };
        countDownTimer.start();
    }
}
