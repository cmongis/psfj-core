/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knop.psfj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import loci.formats.ChannelSeparator;
import loci.formats.FormatException;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LociPrefs;

/**
 *
 * @author cyril
 */
public class DefaultBeadImageLoader implements BeadImageLoader {

    @Override
    public List<BeadImage> load(String fileAddress, boolean fromDisk) throws BeadImageLoadingException {

        
        List<BeadImage> list = new ArrayList<>();
        
        try {
            
            ImageProcessorReader ipr = new ImageProcessorReader(
                    new ChannelSeparator(LociPrefs.makeImageReader()));
            ipr.setId(fileAddress);

            int channelNum = ipr.getSizeC();

            for (int i = 0; i != channelNum; i++) {

                ImageProcessorReader newIpr = new ImageProcessorReader(
                        new ChannelSeparator(LociPrefs.makeImageReader()));
                ipr.setId(fileAddress);

                BeadImage image = new BeadImage(ipr, i);
                list.add(image);
            }

            
        } catch (FormatException ex) {
            Logger.getLogger(DefaultBeadImageLoader.class.getName()).log(Level.SEVERE, null, ex);
            throw new BeadImageLoadingException("Format non supported");
        } catch (IOException ex) {
            Logger.getLogger(DefaultBeadImageLoader.class.getName()).log(Level.SEVERE, null, ex);
            throw new BeadImageLoadingException("Error IO when accessing the file");
        }
        return list;
       
    }

}
