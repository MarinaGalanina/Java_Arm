import javax.media.j3d.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.util.Enumeration;
import java.io.File;
import javax.sound.sampled.Clip;

// class responsible for detecting collisions with the ball
class CollisionDetectorGroup extends Behavior {

    // if the ball interferes with another object
    private boolean inCollision = false;
    private final Group group;

    private WakeupOnCollisionEntry wEnter;
    private WakeupOnCollisionExit wExit;


    public boolean isInCollision()

    {
        return inCollision;
    }

    public CollisionDetectorGroup(Group gp)
    {
        group = gp;
        inCollision = false;
    }

    public void initialize() {
        wEnter = new WakeupOnCollisionEntry(group);
        wExit = new WakeupOnCollisionExit(group);
        wakeupOn(wEnter);
    }

    public void processStimulus(Enumeration criteria)
    {

        inCollision = !inCollision;

        // when the collision with the ball was detected.
        if (inCollision) {

            System.out.println("Collision");

            wakeupOn(wExit);
        }
        //  when the collision is over
        else {

            System.out.println("STOP--Collision");
            wakeupOn(wEnter);
        }
    }


}