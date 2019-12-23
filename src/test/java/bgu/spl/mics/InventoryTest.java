package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import bgu.spl.mics.application.passiveObjects.Inventory;



public class InventoryTest {
    Inventory inv1;
    @BeforeEach
    public void setUp(){
        inv1 = new Inventory();
    }

    @Test
    public void test(){
        //TODO: change this test and add more tests :)
        fail("Not a good test");
    }

    @Test
    public void testGetItem(){
        String[] arr = {"a", "b", "c"};
        inv1.load(arr);

        for (String x:arr) {
            assertTrue(inv1.getItem(x));
            assertFalse(inv1.getItem(x));
        }
    }

    @Test
    public void testPrintToFile(){
        String str = "file name";
        String filePathString = "*.file name.json";
        inv1.printToFile(str);
        File f = new File(filePathString);
        boolean isExist = false;
        if(f.exists() && !f.isDirectory()) {
            isExist = true;
        }
        assertTrue(isExist);
    }


}