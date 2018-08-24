/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knop.psfj.resolution;

import ij.measure.Calibration;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import knop.psfj.PSFj;
import static knop.psfj.resolution.Microscope.BEAD_DIAMETER_KEY;
import static knop.psfj.resolution.Microscope.CALIBRATION_SECTION;
import static knop.psfj.resolution.Microscope.DEFAULT_UNIT;
import static knop.psfj.resolution.Microscope.IDENTIFIER_KEY;
import static knop.psfj.resolution.Microscope.MAGNIFICATION_KEY;
import static knop.psfj.resolution.Microscope.MICROSCOPE_SECTION;
import static knop.psfj.resolution.Microscope.NA_KEY;
import static knop.psfj.resolution.Microscope.PIXEL_DEPTH_KEY;
import static knop.psfj.resolution.Microscope.PIXEL_HEIGHT_KEY;
import static knop.psfj.resolution.Microscope.PIXEL_SIZE_KEY;
import static knop.psfj.resolution.Microscope.PIXEL_WIDTH_KEY;
import static knop.psfj.resolution.Microscope.RI_KEY;
import static knop.psfj.resolution.Microscope.STEP_SIZE_KEY;
import static knop.psfj.resolution.Microscope.UNIT_KEY;
import static knop.psfj.resolution.Microscope.WAVELENGTHS_KEY;
import static knop.psfj.resolution.Microscope.WAVELENGTH_KEY;
import knop.psfj.utils.IniFile;
import knop.psfj.utils.MathUtils;

/**
 *
 * @author cyril
 */
public class MicroscopeConfigurationSaver {

    public static final String WAVELENGTH_DELIMITER = ";";

    public List<Microscope> load(String path) {

        // creating the original objects
        Microscope original = new Microscope();
        IniFile config = new IniFile(path);
        Calibration cal = new Calibration();
        
        Preferences prefs = PSFj.getPreferences();

        // collecting the wavelengths
        String wavelengths[] = config.getStringValue(MICROSCOPE_SECTION, WAVELENGTH_KEY, prefs.get(WAVELENGTHS_KEY, "")).split(WAVELENGTH_DELIMITER);

        if (wavelengths[0] == "") {
            wavelengths[0] = config.getDoubleValue(MICROSCOPE_SECTION, WAVELENGTH_KEY,
                    prefs.getDouble(WAVELENGTH_KEY, 0.0)).toString();
        }

        // transforming into doubles
        List<Double> wavelengthList = Stream
                .of(wavelengths)
                .map(s -> new Double(s))
                .collect(Collectors.toList());

        
        // settng the base variable
        original.setNA(config.getDoubleValue(MICROSCOPE_SECTION, NA_KEY,
                prefs.getDouble(NA_KEY, 0.0)));
        original.setRefraction(config.getDoubleValue(MICROSCOPE_SECTION, RI_KEY,
                prefs.getDouble(RI_KEY, 0.0)));
        original.setBeadDiameter(
                config.getDoubleValue(MICROSCOPE_SECTION, BEAD_DIAMETER_KEY,
                prefs.getDouble(BEAD_DIAMETER_KEY, 0.0)));

        // setting the main calibration object
        original.setCalibration(cal);
        
        
        if (config.getDoubleValue(MICROSCOPE_SECTION, PIXEL_DEPTH_KEY, -1) > 0.0) {

            original.setSpaceBetweenStacks(config.getDoubleValue(MICROSCOPE_SECTION,
                    STEP_SIZE_KEY, prefs.getDouble(STEP_SIZE_KEY, 0.0)));
            original.setCameraPixelSize(config.getDoubleValue(MICROSCOPE_SECTION,
                    PIXEL_SIZE_KEY, prefs.getDouble(PIXEL_SIZE_KEY, 0.0)));
           original.setMagnification(config.getDoubleValue(MICROSCOPE_SECTION,
                    MAGNIFICATION_KEY, prefs.getDouble(MAGNIFICATION_KEY, 0.0)));

            original.calculateCalibrationFromPixelSize();
        } else {
            cal.pixelWidth = config.getDoubleValue(CALIBRATION_SECTION,
                    PIXEL_WIDTH_KEY, prefs.getDouble(PIXEL_WIDTH_KEY, 0.0));
            cal.pixelHeight = config.getDoubleValue(CALIBRATION_SECTION,
                    PIXEL_HEIGHT_KEY, prefs.getDouble(PIXEL_HEIGHT_KEY, 0.0));
            cal.pixelDepth = config.getDoubleValue(CALIBRATION_SECTION,
                    PIXEL_DEPTH_KEY, prefs.getDouble(PIXEL_DEPTH_KEY, 0.0));

        }

        original.setIdentifier(config.getStringValue(MICROSCOPE_SECTION, IDENTIFIER_KEY));

        original.setUnit(config.getStringValue(CALIBRATION_SECTION, UNIT_KEY, DEFAULT_UNIT));

        if (!MathUtils.isMetricUnit(cal.getUnit()) && !cal.getUnit().equals(MathUtils.PIXEL) && !cal.getUnit().equals(MathUtils.PIXELS)) {
            original.setUnit(DEFAULT_UNIT);
        }
        
       return  wavelengthList
               .stream()
                .map(wavelength->original.copyWithWavelength(wavelength))
               .collect(Collectors.toList());

    }

    public void save(String path, List<Microscope> microscopeList) {

    }

}
