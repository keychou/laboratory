package com.mango.statemachinetest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

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
        if (DBG) Log.d(TAG, "testHsm1 E");

        Hsm1 sm = Hsm1.makeHsm1();

        // Send messages
        //Log.d(TAG, "klein------test1---------------");
        //sm.sendMessage(Hsm1.CMD_1);
        Log.d(TAG, "klein------test2---------------");
        sm.sendMessage(Hsm1.CMD_2);

        synchronized (sm) {
            // Wait for the last state machine to notify its done
            try {
                sm.wait();
            } catch (InterruptedException e) {
                Log.d(TAG, "testHsm1: exception while waiting " + e.getMessage());
            }
        }
        if (DBG) Log.d(TAG, "testStateMachineSharedThread X");
    }

    private void tlog(String s) {
        Log.d(TAG, s);
    }

    private void tloge(String s) {
        Log.e(TAG, s);
    }
}
