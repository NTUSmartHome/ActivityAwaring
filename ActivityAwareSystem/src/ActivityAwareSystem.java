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
                String[] request = //sc.next().split(";");
                        server.onRequestData();
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
                    if(preActionResult != actionResult) {
                        switch(actionResult) {
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


