/* 051030-1 Programmiersprachen und -konzepte WS 2017/18
 * Concurrent Programming
 * Hans Moritsch 2017-12-17
 */
import java.util.List;
import java.util.ArrayList;

// ------------------------------------------------------------------------
public class Beispiel1CpImpl implements Beispiel1Cp {
// ------------------------------------------------------------------------

    public Thread setup(Clock clock, Object obj, List<Thread> threads) {

        // ****************************************
        Thread mythread = new Thread( () -> { 
            // run-Methode:

            System.out.println("*** TIMED_WAITING: ***");
            clock.continueAt(0.75);
            System.out.println("-> mythread: sleep"); 
            try { Thread.sleep(500); } 
                catch (InterruptedException ex) { }

            System.out.println("*** BLOCKED: ***");
            // acquire the resource
            synchronized (obj){ }
            //

            System.out.println("*** WAITING: ***"); 
            // acquire the resource and the go to waiting state and wait for someone to call notify or notifyAll on the resource
            // or interrupt mythread
            try {
                synchronized (obj) {

                    System.out.println("-> mythread: wait");
                    obj.wait();
                }
            }
            catch (Exception e ) {
                System.out.println(e);
            }

            System.out.println("*** WAITING - BLOCKED: ***"); 
            // acquire the resource and the go to waiting state and wait for someone to call notify or notifyAll on the resource.
            // after getting notifyed, immediately try to acquire the resource again.
            try {
                synchronized (obj) {
                    System.out.println("-> mythread: wait");
                    obj.wait();

                }
                synchronized (obj) { }
            }
            catch (Exception e ) {
                System.out.println(e);
            }

            System.out.println("*** WAITING - BLOCKED (interrupt status set): ***");
            // acquire the resource and the go to waiting state and wait for someone to call notify or notifyAll on the resource
            // or interrupt mythread
            try {
                synchronized (obj) {
                    System.out.println("-> mythread: wait");
                    obj.wait();
                }
            }
            catch (Exception e ) {
                System.out.println(e);
            }

            System.out.println("*** TERMINATED: ***");
            }, "mythread"); 
        // ****************************************
     
        threads.add(new Thread( () -> { // weiterer Thread
            // run-Methode:
            //acquire the resource, wait 2000 millis and then notify
            try {
                synchronized (obj){
                    Thread.sleep(2000);
                    obj.notify();
                }
            }
            catch (Exception e ) {
                System.out.println(e);
            }
            // ...
        
            } ));

        threads.add(new Thread( () -> { // eventuell weiterer Thread
            // run-Methode:
            //wait 4000 millis, acquire the resource and then immediately notify
            try {
                Thread.sleep(4000);
                synchronized (obj){
                    System.out.println("-> notify");
                    obj.notify();
                }
            }
            catch (Exception e ) { System.out.println(e); }
            // ...

            } ));

        threads.add(new Thread( () -> { // weiterer thread
            // run-Methode:
            //wait 6000 millis, acquire the resource and then immediately notify but
            //keep the lock on object for another 1000 millis to make mythread go directly to blocked state
            try {
                Thread.sleep(6000);
                synchronized (obj){
                    System.out.println("-> notify");
                    obj.notify();
                    Thread.sleep(1000);
                }
            }
            catch (Exception e ) { System.out.println(e); }
            // ...

        } ));


        threads.add(new Thread( () -> { // weiterer thread
            // run-Methode:
            //wait 9000 millis, acquire the resource and then immediately interrupt mythread but
            //keep the lock on object for another 1000 millis to make mythread go to blocked-ir state.
            try {
                Thread.sleep(9000);
                synchronized (obj) {
                    System.out.println("-> interrupt");
                    mythread.interrupt();
                    Thread.sleep(1000);
                }
            }
            catch (Exception e ) { /*System.out.println(e);*/ }
            // ...

        } ));

        return mythread;
        }

    }
