/*
 * Utilities for Gaussian fitting ImageJ plugins
 * Needs org.apache.commons.math and jfreechart
 * Includes the actual Gaussian functions
 */

package knop.psfj.resolution.Gaussian2DFit;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.SingularValueDecompositionImpl;
import org.apache.commons.math.stat.StatUtils;



/**
 *
 * @author nico
 */
public class GaussianUtils {

   public static final int INT = 0;
   public static final int BGR = 1;
   public static final int XC = 2;
   public static final int YC = 3;
   public static final int S = 4;
   public static final int S1 = 4;
   public static final int S2 = 5;
   public static final int S3 = 6;


   public static double sqr(double val) {
      return val*val;
   }

   public static double cube(double val) {
      return val * val * val;
   }

 /**
    * Gaussian function of the form:
    * A *  exp(-((x-xc)^2+(y-yc)^2)/(2 sigy^2))+b
    * A = params[INT]  (amplitude)
    * b = params[BGR]  (background)
    * xc = params[XC]
    * yc = params[YC]
    * sig = params[S]
    */
   public static double gaussian(double[] params, int x, int y) {
      if (params.length < 5) {
                       // Problem, what do we do???
                       //MMScriptException e;
                       //e.message = "Params for Gaussian function has too few values"; //throw (e);
      }

      double exponent = (sqr(x - params[XC])  + sqr(y - params[YC])) / (2 * sqr(params[S]));
      double res = params[INT] * Math.exp(-exponent) + params[BGR];
      return res;
   }

   /**
    * Derivative (Jacobian) of the above function
    *
    * @param params - Parameters to be optimized
    * @param x - x position in the image
    * @param y - y position in the image
    * @return - array with the derivates for each of the parameters
    */
   public static double[] gaussianJ(double[] params, int x, int y) {
      double q = gaussian(params, x, y) - params[BGR];
      double dx = x - params[XC];
      double dy = y - params[YC];
      double[] result = {
         q/params[INT],
         1.0,
         dx * q/sqr(params[S]),
         dy * q/sqr(params[S]),
         (sqr(dx) + sqr(dy)) * q/cube(params[S])
      };
      return result;
   }


   /**
    * Gaussian function of the form:
    * f = A * e^(-((x-xc)^2/sigma_x^2 + (y-yc)^2/sigma_y^2)/2) + b
    * A = params[INT]  (total intensity)
    * b = params[BGR]  (background)
    * xc = params[XC]
    * yc = params[YC]
    * sig_x = params[S1]
    * sig_y = params[S2]
    */
   public static double gaussian2DXY(double[] params, int x, int y) {
      if (params.length < 6) {
                       // Problem, what do we do???
                       //MMScriptException e;
                       //e.message = "Params for Gaussian function has too few values"; //throw (e);
      }

      double exponent = ( (sqr(x - params[XC]))/(2*sqr(params[S1])))  +
              (sqr(y - params[YC]) / (2 * sqr(params[S2])));
      double res = params[INT] * Math.exp(-exponent) + params[BGR];
      return res;
   }

    /**
    * Derivative (Jacobian) of the above function
    *
     *
     * p = A,b,xc,yc,sigma_x,sigma_y
         f = A * e^(-((x-xc)^2/sigma_x^2 + (y-yc)^2/sigma_y^2)/2) + b
         J = {
          q/A,
          1,
          dx*q/sigma_x^2,
          dy*q/sigma_y^2,
          dx^2*q/sigma_x^3,
          dy^2*q/sigma_y^3
         }
    * @param params - Parameters to be optimized
    * @param x - x position in the image
    * @param y - y position in the image
    * @return - array with the derivates for each of the parameters
    */
   public static double[] gaussianJ2DXY(double[] params, int x, int y) {
      double q = gaussian2DXY(params, x, y) - params[BGR];
      double dx = x - params[XC];
      double dy = y - params[YC];
      double[] result = {
         q/params[INT],
         1.0,
         dx * q/sqr(params[S1]),
         dy * q/sqr(params[S2]),
         sqr(dx) * q /cube(params[S1]),
         sqr(dy) * q /cube(params[S2])
      };
      return result;
   }

   /**
    * Gaussian function of the form:
    * f =  A * e^(-(a*(x-xc)^2 + c*(y-yc)^2 + 2*b*(x-xc)*(y-yc))) + B
    * A = params[INT]  (total intensity)
    * B = params[BGR]  (background)
    * xc = params[XC]
    * yc = params[YC]
    * a = params[S1]
    * b = params[S2]
    * c = params[S3]
    */
   
   
   
   public static double gaussian2DEllips(double[] params, int x, int y) {
      if (params.length < 7) {
                       // Problem, what do we do???
                       //MMScriptException e;
                       //e.message = "Params for Gaussian function has too few values"; //throw (e);
      }

      double exponent = ( (params[S1] * sqr(x - params[XC])) +
                          (params[S3] * sqr(y - params[YC])) +
                          (2.0 * params[S2] * (x - params[XC]) * (y - params[YC]))
                           )  ;
      double res = params[INT] * Math.exp(-exponent) + params[BGR];
      return res;
   }


    /**
    * Derivative (Jacobian) of gaussian2DEllips
    * p = A,B,xc,yc,a,b,c
    * J = {
    * q/A,
    * 1,
    * (a*dx + b*dy)*q,
    * (b*dx + c*dy)*q,
    * -1/2*dx^2*q,
    * -dx*dy*q,
    * -1/2*dy^2*q
    * }
    * @param params - Parameters to be optimized
    * @param x - x position in the image
    * @param y - y position in the image
    * @return - array with the derivates for each of the parameters
    */
   public static double[] gaussianJ2DEllips(double[] params, int x, int y) {
      double q = gaussian2DEllips(params, x, y) - params[BGR];
      double dx = x - params[XC];
      double dy = y - params[YC];
      double[] result = {
         q/params[INT],
         1.0,
         (params[S1] * dx + params[S2] * dy) * q,
         (params[S2] * dx + params[S3] * dy) * q,
         -0.5 * sqr(dx) * q,
         -dx * dy * q,
         -0.5 * sqr(dy) * q
      };
      return result;
   }

   /**
    * Converts paramers from 2DEllipse fit to theta, sigma_x and sigma_y
    *
    *
    * @param a - params[S1] from Gaussian fit
    * @param b - params[S2] from Gaussian fit
    * @param c - params[S3] from Gaussian fit
    * @return double[3] containing, theta, sigmax, and sigmay in that order
    */
   public static double[] ellipseParmConversion(double a, double b, double c) {
      double[] result = new double[6];

      
      
   
     
      double t = Math.sqrt ( 4.0*sqr(b) + sqr(a-c) );
     
      
      double s = Math.sqrt(Math.abs(sqr(b) - (a*c)));
     // System.out.printf(" - a = %.3f,\n - t = %.3f,\n - c = %.3f\n - s = %.3f\n",a,t,c,s);
      double sx = Math.sqrt(Math.abs(-a+t-c))/2.0/s;
      double sy = Math.sqrt(Math.abs(-a-t-c))/2.0/s;
      
      double theta = Math.sqrt(1.0+((a-c)/t))/Math.sqrt(2.0);
      theta = Math.acos(theta);
     // theta = Math.PI/2 - theta;
      double theta_sign = 4*b*sqr(sx)*sqr(sy)/(sqr(sx)-sqr(sy));
      
      
      theta_sign = Math.signum(-0.5 * Math.asin(theta_sign));
      
      theta = ((Math.PI/2)-theta) * theta_sign;
      
      
      result[0] = theta;
      result[1] = sx;
      result[2] = sy;
   
      return result;
   }

   
   /**
    * Linear Regression to find the best line between a set of points
    * returns an array where [0] = slope and [1] = offset
    * Input: arrays with x and y data points
    * Not used anymore
    *
   public double[] fitLine(Vector<Point2D.Double> xyPoints) {
      double[][] xWithOne = new double[xyPoints.size()][2];
      double[][] yWithOne = new double[xyPoints.size()][2];
      for (int i =0; i< xyPoints.size(); i++) {
         xWithOne[i][0] = xyPoints.get(i).getX();
         xWithOne[i][1] = 1;
         yWithOne[i][0] = xyPoints.get(i).getY();
         yWithOne[i][1] = 1;
      }

      Array2DRowRealMatrix xM = new Array2DRowRealMatrix(xWithOne);
      Array2DRowRealMatrix yM = new Array2DRowRealMatrix(yWithOne);

      QRDecompositionImpl qX = new QRDecompositionImpl(xM);
      BlockRealMatrix mX = (BlockRealMatrix) qX.getSolver().solve(yM);

      RealMatrix theY = xM.multiply(mX);
      double ansX = theY.subtract(yM).getColumnVector(0).getNorm();
      print ("Answer X: " + ansX);

      QRDecompositionImpl qY = new QRDecompositionImpl(yM);
      BlockRealMatrix mY = (BlockRealMatrix) qY.getSolver().solve(xM);

      RealMatrix theX = yM.multiply(mY);
      double ansY = theX.subtract(xM).getColumnVector(0).getNorm();
      print ("Answer Y: " + ansY);

      double[][] res = mX.getData();
      double[] ret = new double[2];
      ret[0] = res[0][0];
      ret[1] = res[1][0];

      if (ansY < ansX) {
         res = mY.getData();
         ret[0] = 1 / res[0][0];
         ret[1] = - res[1][0]/res[0][0];
      }

      return ret;
   }

   public AffineTransform computeAffineTransform(double a, double b) {
      AffineTransform T = new AffineTransform();
      T.rotate(-Math.atan(a));
      T.translate(0, -b);
      return T;
   }
   */

}
