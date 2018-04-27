package com.zhoukaihffoxmail.statemachinetest;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.util.Collection;
import java.util.Iterator;

import android.os.Debug;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.internal.util.StateMachine.LogRec;

import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import junit.framework.TestCase;

public class StateMachineTest extends Activity {

    private static final String ENTER = "enter";
    private static final String EXIT = "exit";
    private static final String ON_QUITTING = "ON_QUITTING";

    private static final int TEST_CMD_1 = 1;
    private static final int TEST_CMD_2 = 2;
    private static final int TEST_CMD_3 = 3;
    private static final int TEST_CMD_4 = 4;
    private static final int TEST_CMD_5 = 5;
    private static final int TEST_CMD_6 = 6;

    private static final boolean DBG = true;
    private static final boolean WAIT_FOR_DEBUGGER = false;
    private static final String TAG = "StateMachineTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_machine_test);

        try{
            testHsm1();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void testHsm1() throws Exception {
        if (DBG) tlog("testHsm1 E");

        Hsm1 sm = Hsm1.makeHsm1();

        // Send messages
        sm.sendMessage(Hsm1.CMD_1);
        sm.sendMessage(Hsm1.CMD_2);

        synchronized (sm) {
            // Wait for the last state machine to notify its done
            try {
                sm.wait();
            } catch (InterruptedException e) {
                tloge("testHsm1: exception while waiting " + e.getMessage());
            }
        }

        dumpLogRecs(sm);

        assertEquals(7, sm.getLogRecCount());

        LogRec lr = sm.getLogRec(0);
        assertEquals(Hsm1.CMD_1, lr.getWhat());
        assertEquals(sm.mS1, lr.getState());
        assertEquals(sm.mS1, lr.getOriginalState());

        lr = sm.getLogRec(1);
        assertEquals(Hsm1.CMD_2, lr.getWhat());
        assertEquals(sm.mP1, lr.getState());
        assertEquals(sm.mS1, lr.getOriginalState());

        lr = sm.getLogRec(2);
        assertEquals(Hsm1.CMD_2, lr.getWhat());
        assertEquals(sm.mS2, lr.getState());
        assertEquals(sm.mS2, lr.getOriginalState());

        lr = sm.getLogRec(3);
        assertEquals(Hsm1.CMD_3, lr.getWhat());
        assertEquals(sm.mS2, lr.getState());
        assertEquals(sm.mS2, lr.getOriginalState());

        lr = sm.getLogRec(4);
        assertEquals(Hsm1.CMD_3, lr.getWhat());
        assertEquals(sm.mP2, lr.getState());
        assertEquals(sm.mP2, lr.getOriginalState());

        lr = sm.getLogRec(5);
        assertEquals(Hsm1.CMD_4, lr.getWhat());
        assertEquals(sm.mP2, lr.getState());
        assertEquals(sm.mP2, lr.getOriginalState());

        lr = sm.getLogRec(6);
        assertEquals(Hsm1.CMD_5, lr.getWhat());
        assertEquals(sm.mP2, lr.getState());
        assertEquals(sm.mP2, lr.getOriginalState());

        if (DBG) tlog("testStateMachineSharedThread X");
    }

    private void tlog(String s) {
        Log.d(TAG, s);
    }

    private void tloge(String s) {
        Log.e(TAG, s);
    }
}
