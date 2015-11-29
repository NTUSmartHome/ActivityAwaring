import GUI.LabelingScreen;
import adaption.KNNModel;
import adaption.SimilarityFunction;
import com.datumbox.framework.machinelearning.classification.SupportVectorMachineTrain;
import com.sun.media.jfxmedia.MediaManager;
import dpmm.GDPMMOnline;
import dpmm.MDPMMOnline;
import elements.FileFormat;
import jaco.mp3.player.MP3Player;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import wearable.SwingMotionFeatureExtration;

import java.applet.AudioClip;
import java.io.File;
import java.util.Scanner;
import java.util.Vector;


public class ActivityAwareSystem {
    public static void main(String args[]) {
        boolean Train = false;
        boolean Online = true;
        boolean Label = false;
        boolean SVM = false;
        //String Path = "7.08.MingJe_v1";
        String Path = "DEMO";
        //String Path = "7.07.MingJe_v1";
        int timewindow = 10;
        int overlap = 5;
        if (Train) {
            new BuildModel(Path, timewindow, overlap);
        }
        if (Label) {
            new LabelingScreen(Path, "MeaningfulAction", 0);
        }
        if (SVM) {
            SupportVectorMachineTrain SVMModel = new SupportVectorMachineTrain(Path, "SVM");
        }
        if (Online) {
            SocketServer server = new SocketServer();
            server.start();

            FileFormat LowAct = new FileFormat(Path, "DPMM/SwingMotion");
            FileFormat HighAct = new FileFormat(Path, "DPMM/MeaningfulAction");

            MDPMMOnline SwingMotionPredict = new MDPMMOnline(Path, LowAct.getResult());
            MDPMMOnline ActionPredict = new MDPMMOnline(Path, HighAct.getResult());

            SwingMotionFeatureExtration swingMotionFeatureExtration = new SwingMotionFeatureExtration(true, true);

            //  int numOfActionFeature = ActionPredict.getF();

            boolean actionPredictFlag = false;
            Vector<Integer> swingMotionOnline = new Vector<Integer>();

            int actionResult = -1;
            int preActionResult = -1;
            //Scanner sc = new Scanner(System.in);


            // SOUND
            MP3Player[] sound = new MP3Player[4];
            sound[0] = new MP3Player(new File("sound/EXERCISE.mp3"));
            sound[1] = new MP3Player(new File("sound/WALKING.mp3"));
            sound[2] = new MP3Player(new File("sound/SWEEPING.mp3"));
            sound[3] = new MP3Player(new File("sound/FALLING.mp3"));
            while (true) {

                //sound[2].play();
                String[] request = {"T:11,21,19","Label:PlayPad","Ax:-9.347861,-10.1164,-11.876139,-14.009372,-15.690102,-16.05402,-11.2249155,-8.394969,-3.3910875,-2.8092964,0.26964697,1.0549451,3.916016;","Ay:-1.8056769,-0.8575731,-1.070657,0.53824645,-0.8575731,-2.7872996,-1.925387,-5.6842837,-1.3028946,0.73935944,6.0329394,6.8326025,5.956325;","Az:4.8130937,5.291934,9.457845,10.506505,6.7212725,4.9663224,9.007735,8.605509,1.8179473,-3.0925603,-15.918299,-10.670209,-5.340716;","Gx:-0.36302084,-0.98446584,-2.6931777,-4.868609,-7.673566,-3.920655,-3.340809,-2.10585,-6.849961,-6.5840545,-2.5833437;","Gy:-1.0116999,-0.1467945,-0.3362357,1.2029368,-2.9314008,-2.3952491,-6.6006646,-3.4144309,-4.596867,-0.2160767,-1.2156559;","Gz:0.96426475,1.7884687,6.095488,7.0169563,-0.12479776,2.4809916,0.25169045,-7.6719203,-10.2596035,-19.103334,-3.7627873;","Y:47.339527,39.580658,28.769207,23.290724,30.11963,36.239178,27.79215,46.54878,55.104786,44.038624,-85.13241,-96.45799,-68.1681;","P:9.744744,4.2956605,4.033901,-1.7605413,2.876191,9.41762,7.6197333,25.305744,18.707176,-10.035336,-20.753681,-32.50679,-41.968143;","R:62.756645,62.38573,51.467106,53.131474,66.810844,72.81052,51.253765,44.290462,61.804436,137.74783,-179.02953,-174.3536,-143.74973,"};
                        //server.onRequestData();
                String line = swingMotionFeatureExtration.readRawDataOnline(request);
                System.out.print(line + "\n");
                //System.out.println(line);
                //System.out.println("test");
                // listen smart watch message and extract features
                String[] feature = line.split(",");
                Double[] featureDoubles = new Double[feature.length - 1];
                for (int i = 0; i < featureDoubles.length; i++)
                    featureDoubles[i] = Double.valueOf(feature[i]);

                // reconize input instance
                int swingMotionResult = SwingMotionPredict.predict(featureDoubles);

                // add the instance result to the low action list (until fulfill the size to timewindow)
                swingMotionOnline.add(swingMotionResult);


                if (swingMotionOnline.size() == timewindow) {
                    actionResult = ActionPredict.predict(generateActionFeature(swingMotionOnline, SwingMotionPredict.getC()));
                    for (int i = 0; i < (timewindow - overlap); i++) {
                        swingMotionOnline.remove(0);
                    }
                    if (preActionResult != actionResult) {
                        switch (actionResult) {
                            case 0:
                                System.out.println("Exercise");
                                sound[0].play();
                                break;
                            case 2:
                                System.out.println("WearShoes");
                                sound[3].play();
                                break;
                            case 3:
                                System.out.println("Walk");
                                sound[1].play();
                                break;

                        }
                        // ¤W¦¸DEMO
                        /*switch (actionResult){
                            case 0: System.out.println("Exercise");
                                sound[0].play();
                                break;
                            case 1: System.out.println("Exercise");
                                sound[0].play();
                                break;
                            case 2: System.out.println("Sweep");
                                sound[2].play();
                                break;
                            case 6: System.out.println("Falling");
                                sound[3].play();
                                break;
                            case 3: System.out.println("Walk");
                                sound[1].play();
                                break;
                            default:
                                break;

                        }*/
                    } else {
                        preActionResult = actionResult;
                    }

                    System.out.println("Activity Id is " + actionResult);
                }

            }


        }


    }

    private static Vector<Double> generateActionFeature(Vector<Integer> swingMotionOnline, int numOfActionClu) {
        Vector<Double> actionFeature = new Vector<Double>();
        for (int i = 0; i < numOfActionClu; i++)
            actionFeature.add(0.0);

        for (int i = 0; i < swingMotionOnline.size(); i++) {
            int index = swingMotionOnline.get(i);
            actionFeature.set(index, actionFeature.get(index) + 1);
        }
        return actionFeature;
    }

    double[] getWearableFeature(String Instance) {
        String[] tmp = Instance.split(",");
        double[] f = new double[tmp.length - 1];
        for (int i = 0; i < f.length; i++) {
            f[i] = Double.valueOf(tmp[i]);
        }
        return f;
    }

    String getWearableLabel(String Instance) {
        String[] tmp = Instance.split(",");
        return tmp[tmp.length - 1];
    }


}


