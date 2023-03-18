package com.mango.statemachinetest;

import android.os.Message;

import com.android.internal.statemachine.State;
import com.android.internal.statemachine.StateMachine;

/**
 * Created by zhoukai on 17-3-7.
 */

class Hsm1 extends StateMachine {
    public static final int CMD_1 = 1;
    public static final int CMD_2 = 2;
    public static final int CMD_3 = 3;
    public static final int CMD_4 = 4;
    public static final int CMD_5 = 5;

    public static Hsm1 makeHsm1() {
        //log("makeHsm1 E");
        Hsm1 sm = new Hsm1("hsm1");
        sm.start();
        //log("makeHsm1 X");
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
       public void enter() {
            log("mP1.enter");
        }
        public boolean processMessage(Message message) {
            boolean retVal;
            log("mP1.processMessage what=" + message.what);
            switch(message.what) {
                case CMD_2:
                    // CMD_2 will arrive in mS2 before CMD_3
                    sendMessage(obtainMessage(CMD_3));
                    deferMessage(message);
                    transitionTo(mS2);
                    retVal = HANDLED;
                    break;
                default:
                    // Any message we don't understand in this state invokes unhandledMessage
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }
        public void exit() {
            log("mP1.exit");
        }
    }

    class S1 extends State {
        public void enter() {
            log("mS1.enter");
        }
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
        public void exit() {
            log("mS1.exit");
        }
    }

    class S2 extends State {
        public void enter() {
            log("mS2.enter");
        }
       public boolean processMessage(Message message) {
            boolean retVal;
            log("mS2.processMessage what=" + message.what);
            switch(message.what) {
                case(CMD_2):
                    sendMessage(obtainMessage(CMD_4));
                    retVal = HANDLED;
                    break;
                case(CMD_3):
                    deferMessage(message);
                    transitionTo(mP2);
                    retVal = HANDLED;
                    break;
                default:
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }
        public void exit() {
            log("mS2.exit");
        }
    }

    class P2 extends State {
        public void enter() {
            log("mP2.enter");
            sendMessage(obtainMessage(CMD_5));
        }
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
        public void exit() {
            log("mP2.exit");
        }
    }

    void onHalting() {
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