/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knop.psfj;

import org.junit.Test;

/**
 *
 * @author cyril
 */
public class SimpleBeadAnalysisTest {
    
    
    @Test
    public void testMonoChannel() {
        
        BeadImageManager manager = new BeadImageManager();
        
        manager.add("./src/test/resources/ch1_ApoTIRF_60x.tif");
        
    }
    
}
