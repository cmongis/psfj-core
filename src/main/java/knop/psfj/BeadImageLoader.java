/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knop.psfj;

import java.util.List;

/**
 *
 * @author cyril
 */
public interface BeadImageLoader {
    
    
    List<BeadImage> load(String fileAddress, boolean forceLoadingFromDisk) throws BeadImageLoadingException;
    
    
}
