package com.zhoukaihffoxmail.statemachinetest;

import android.os.Message;
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


/**
 * Created by zhoukai on 17-3-7.
 */

static class Hsm1 extends StateMachine {
private static final String HSM1_TAG = "hsm1";

public static final int CMD_1 = 1;
public static final int CMD_2 = 2;
public static final int CMD_3 = 3;
public static final int CMD_4 = 4;
public static final int CMD_5 = 5;

public static Hsm1 makeHsm1() {
        Log.d(HSM1_TAG, "makeHsm1 E");
        Hsm1 sm = new Hsm1(HSM1_TAG);
        sm.start();
        Log.d(HSM1_TAG, "makeHsm1 X");
        return sm;
        }

        Hsm1(String name) {
        super(name);
        log("ctor E");

        // Add states, use indentation to show hierarchy
        addState(mP1);
        addState(mS1, mP1);
        addState(mS2, mP1);
        addState(mP2);

        // Set the initial state
        setInitialState(mS1);
        log("ctor X");
        }

class P1 extends State {
    @Override
    public void enter() {
        log("P1.enter");
    }
    @Override
    public void exit() {
        log("P1.exit");
    }
    @Override
    public boolean processMessage(Message message) {
        boolean retVal;
        log("P1.processMessage what=" + message.what);
        switch(message.what) {
            case CMD_2:
                // CMD_2 will arrive in mS2 before CMD_3
                sendMessage(CMD_3);
                deferMessage(message);
                transitionTo(mS2);
                retVal = true;
                break;
            default:
                // Any message we don't understand in this state invokes unhandledMessage
                retVal = false;
                break;
        }
        return retVal;
    }
}

class S1 extends State {
    @Override
    public void enter() {
        log("S1.enter");
    }
    @Override
    public void exit() {
        log("S1.exit");
    }
    @Override
    public boolean processMessage(Message message) {
        log("S1.processMessage what=" + message.what);
        if (message.what == CMD_1) {
            // Transition to ourself to show that enter/exit is called
            transitionTo(mS1);
            return HANDLED;
        } else {
            // Let parent process all other messages
            return NOT_HANDLED;
        }
    }
}

class S2 extends State {
    @Override
    public void enter() {
        log("S2.enter");
    }
    @Override
    public void exit() {
        log("S2.exit");
    }
    @Override
    public boolean processMessage(Message message) {
        boolean retVal;
        log("S2.processMessage what=" + message.what);
        switch(message.what) {
            case(CMD_2):
                sendMessage(CMD_4);
                retVal = true;
                break;
            case(CMD_3):
                deferMessage(message);
                transitionTo(mP2);
                retVal = true;
                break;
            default:
                retVal = false;
                break;
        }
        return retVal;
    }
}

class P2 extends State {
    @Override
    public void enter() {
        log("P2.enter");
        sendMessage(CMD_5);
    }
    @Override
    public void exit() {
        log("P2.exit");
    }
    @Override
    public boolean processMessage(Message message) {
        log("P2.processMessage what=" + message.what);
        switch(message.what) {
            case(CMD_3):
                break;
            case(CMD_4):
                break;
            case(CMD_5):
                transitionToHaltingState();
                break;
        }
        return HANDLED;
    }
}

    @Override
    protected void onHalting() {
        log("halting");
        synchronized (this) {
            this.notifyAll();
        }
    }

    P1 mP1 = new P1();
    S1 mS1 = new S1();
    S2 mS2 = new S2();
    P2 mP2 = new P2();
}