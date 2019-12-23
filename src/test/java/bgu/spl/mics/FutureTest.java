package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {
    Future<Integer> future;
    Future<String> futureS;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
        futureS = new Future<>();
    }

    @Test
    public void get() {
        assertNull(future.get());
        assertNull(futureS.get());

        future.resolve(6);
        assertEquals(new Integer(6), future.get());

        futureS.resolve("Game Of Thrones");
        assertEquals("Game Of Thrones", futureS.get());
    }

    @Test
    public void resolve() {
        future.resolve(893);
        assertEquals(new Integer(893), future.get());
    }

    @Test
    public void isDone() {
        assertFalse(future.isDone());
        future.resolve(999);
        assertTrue(future.isDone());
    }

    @Test
    public void get1() {
        TimeUnit time = TimeUnit.MILLISECONDS;
        Object o = null;
        o = future.get(666,time);
        assertNull(o);

        future.resolve(new Integer(5));
        assertEquals(new Integer(5), future.get(893, time));
    }

}
