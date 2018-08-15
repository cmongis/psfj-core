/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knop.psfj;

import static org.junit.Assert.assertEquals;
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
        
        manager.autoFocus(0);
         manager.setThresholdValue(1533);
        manager.setFrameSize(17);
       
        
        manager.processBeadImages();
        
        int validBeads = manager.getBeadFrameList().getValidBeadFrameCount();
        int invalidBeads = manager.getBeadFrameList().getNonValidBeadFrameCount();
        FovDataSet dataset = manager.getDataSet();
       // double xMedian = new Double(dataset.getColumnMedian(PSFj.));
       // double yMedian = new Double(dataset.getColumnMedian(PSFj.FWHM_KEY[1]));
       // double zMedian = new Double(dataset.getColumnMedian(PSFj.FWHM_KEY[2]));
        
        assertEquals("Valid beads",29,validBeads);
        assertEquals("Invalid beads",35,invalidBeads);
        
        
    }
    
}
