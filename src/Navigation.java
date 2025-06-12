
import swiftbot.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Navigation {

    static SwiftBotAPI swiftBot;
    public static ArrayList<String> CommandLogHistory = new ArrayList<>();

    public static void main(String[] args) {

        // ssh pi@192.168.60.XX (at school)
        // ssh pi@192.168.0.95 (at home)
        // javac -cp "SwiftBotAPI-5.1.0.jar" Navigation.java
        // java -cp "SwiftBotAPI-5.1.0.jar": Navigation
        try {
            swiftBot = new SwiftBotAPI();
        } catch (Exception e) {
            /*
             * Outputs a warning if I2C is disabled. This only needs to be turned on once,
             * so you won't need to worry about this problem again!
             */
            System.out.println("\nI2C disabled!");
            System.out.println("Run the following command:");
            System.out.println("sudo raspi-config nonint do_i2c 0\n");
            System.exit(5);
        }

        Integer DisplayIntro = 0;
        String command = "";
        // Where STEPS are the number of moves to retrace and I is the Index of the command to execute
        Integer speed, duration, steps = 0, i = 0;

        // reading from System.in
        Scanner reader = new Scanner(System.in);

        // ASCII Art
        System.out.println("░░░░█▐▄▒▒▒▌▌▒▒▌░▌▒▐▐▐▒▒▐▒▒▌▒▀▄▀▄░\r\n"
                + "░░░█▐▒▒▀▀▌░▀▀▀░░▀▀▀░░▀▀▄▌▌▐▒▒▒▌▐░\r\n"
                + "░░▐▒▒▀▀▄▐░▀▀▄▄░░░░░░░░░░░▐▒▌▒▒▐░▌\r\n"
                + "░░▐▒▌▒▒▒▌░▄▄▄▄█▄░░░░░░░▄▄▄▐▐▄▄▀░░\r\n"
                + "░░▌▐▒▒▒▐░░░░░░░░░░░░░▀█▄░░░░▌▌░░░\r\n"
                + "▄▀▒▒▌▒▒▐░░░░░░░▄░░▄░░░░░▀▀░░▌▌░░░  NYAAAAAAAAAAAAAAAAAAAAAAAAA\r\n"
                + "▄▄▀▒▐▒▒▐░░░░░░░▐▀▀▀▄▄▀░░░░░░▌▌░░░\r\n"
                + "░░░░█▌▒▒▌░░░░░▐▒▒▒▒▒▌░░░░░░▐▐▒▀▀▄\r\n"
                + "░░▄▀▒▒▒▒▐░░░░░▐▒▒▒▒▐░░░░░▄█▄▒▐▒▒▒\r\n"
                + "▄▀▒▒▒▒▒▄██▀▄▄░░▀▄▄▀░░▄▄▀█▄░█▀▒▒▒▒");

        swiftBot.enableButton(Button.X, ()
                -> {
            System.out.println("\nButton 'X' Pressed.\nTerminating Program...");
            System.exit(0);
        });

        while (true) {
            // If the intro has already been displayed it won't be displayed again
            if (DisplayIntro == 0) {
                Introduction();
                DisplayIntro += 1;
            } else {
                System.out.println("");
            }

            speed = 0;
            duration = 0;

            // Taking user inputs
            System.out.println("Enter your command: ");
            // Reading user command
            command = reader.next();

            // choosing what text to display to the user based on the command
            switch (command) {
                case "S":
                    break;

                case "X":
                    // Ends Program
                    System.out.println("\nTerminating Program...");
                    reader.close();
                    System.exit(5);
                    break;

                case "W":
                    break;

                case "T":
                    // Taking user steps
                    System.out.println("Enter number of steps to retrace: ");
                    steps = Integer.valueOf(reader.next());
                    break;

                default:
                    try {
                        // Taking user speed and duration
                        System.out.println("Enter your speed: ");
                        speed = Integer.valueOf(reader.next());

                        System.out.println("Enter your duration: ");
                        duration = Integer.valueOf(reader.next());

                    } catch (IllegalArgumentException e) {
                        System.out.println("\nInvalid Speed/Duration... \n");
                        flashLights();
                        //e.printStackTrace();
                    }

                    break;
            }

            System.out.println("");

            // Performing the appropriate action on the swiftbot
            switch (command) {
                // You can scan QR codes from your phone, the camera is trash so it might take a few tries
                case "S":
                    try {
                        System.out.println("Scanning... ");
                        String decodedText = ScanQR(); // in form "F 4 50"
                        List<String> input = Arrays.asList(decodedText.split(" "));
                        command = input.get(0);
                        duration = Integer.valueOf(input.get(1));
                        speed = Integer.valueOf(input.get(2));
                        // T needs to be in the form "T 4 0" from being scanned because i can't remove the speed in the case of T and it causes an error...  i've tried for 2 hours it won't work i give up OH MY GOD

                        steps = duration;

                        switch (command) {
                            case "F":
                                // Same as other case
                                if (speed <= 100 && speed > 0 && duration <= 6) {
                                    System.out.println("Moving Forwards...");
                                    ForwardandBackward(command, speed, duration);

                                } else if (speed > 100 || speed <= 0) {
                                    System.out.println("Invalid Speed");
                                    flashLights();
                                } else {
                                    System.out.println("Invalid Duration");
                                    flashLights();
                                }
                                break;

                            case "B":
                                // Same as other case
                                if (speed <= 100 && speed > 0 && duration <= 6) {
                                    System.out.println("Moving Backwards...");
                                    ForwardandBackward(command, -speed, duration);
                                } else if (speed > 100 || speed <= 0) {
                                    System.out.println("Invalid Speed");
                                    flashLights();
                                } else {
                                    System.out.println("Invalid Duration");
                                    flashLights();
                                }

                                break;

                            case "R":
                                // Same as other case
                                if (speed <= 100 && duration <= 6) {
                                    System.out.println("Moving Right...");
                                    // For a right turn the right wheel must stay still and the right wheel must move
                                    RightandLeft(command, speed, 0, duration, speed);
                                } else if (speed > 100 || speed <= 0) {
                                    System.out.println("Invalid Speed");
                                    flashLights();
                                } else {
                                    System.out.println("Invalid Duration");
                                    flashLights();
                                }
                                break;

                            case "L":
                                // Same as other case
                                if (speed <= 100 && duration <= 6) {
                                    System.out.println("Moving Left...");
                                    // For a left turn the left wheel must stay still and the right wheel must move
                                    RightandLeft(command, 0, speed, duration, speed);
                                } else if (speed > 100 || speed <= 0) {
                                    System.out.println("Invalid Speed");
                                    flashLights();
                                } else {
                                    System.out.println("Invalid Duration");
                                    flashLights();
                                }
                                break;

                            case "T":
                                // Duration can be used in place of the steps here for scan
                                steps *= 3;
                                if (steps <= CommandLogHistory.size() && steps > 0) {
                                    Traceback(steps, i);
                                    System.out.println("\nEnding Traceback... \n");
                                    System.out.println("Command Log History = " + CommandLogHistory);

                                } else {
                                    System.out.println("Incorrect Steps: '" + steps / 3 + "' is an invalid number of movements");
                                    flashLights();
                                }

                                break;

                            default:
                                break;
                        }

                    } catch (IllegalArgumentException e) {
                        System.out.println("Error in Scan... ");
                        flashLights();
                        //e.printStackTrace();
                    }

                    break;

                case "T":
                    // Where steps is the number of steps the user wants to retrace, and i follow the index of the list
                    steps *= 3;
                    if (steps <= CommandLogHistory.size() && steps > 0) {
                        Traceback(steps, i);
                        System.out.println("\nEnding Traceback... \n");
                        System.out.println("Command Log History = " + CommandLogHistory);

                    } else {
                        System.out.println("Incorrect Steps: '" + steps / 3 + "' is an invalid number of movements");
                        flashLights();
                    }

                    break;

                case "W":
                    System.out.println("\nWriting Command Log History to a text file... \n");
                    // Gets machines current time
                    LocalTime now = LocalTime.now();
                    System.out.println(now);
                    System.out.println("Command Log History = " + CommandLogHistory);

                    CreateFile();
                    WriteToFile(CommandLogHistory, now);

                    break;

                case "F":
                    if (speed <= 100 && speed > 0 && duration <= 6) {
                        System.out.println("Moving Forwards...");
                        ForwardandBackward(command, speed, duration);

                    } else if (speed > 100 || speed <= 0) {
                        System.out.println("Invalid Speed");
                        flashLights();
                    } else {
                        System.out.println("Invalid Duration");
                        flashLights();
                    }
                    break;

                case "B":
                    if (speed <= 100 && speed > 0 && duration <= 6) {
                        System.out.println("Moving Backwards...");
                        // to go backwards you just need to - the normal speed
                        ForwardandBackward(command, -speed, duration);
                    } else if (speed > 100 || speed <= 0) {
                        System.out.println("Invalid Speed");
                        flashLights();
                    } else {
                        System.out.println("Invalid Duration");
                        flashLights();
                    }

                    break;

                case "R":
                    if (speed <= 100 && duration <= 6) {
                        System.out.println("Moving Right...");
                        // For a right turn the right wheel must stay still and the right wheel must move
                        RightandLeft(command, speed, 0, duration, speed);
                    } else if (speed > 100 || speed <= 0) {
                        System.out.println("Invalid Speed");
                        flashLights();
                    } else {
                        System.out.println("Invalid Duration");
                        flashLights();
                    }

                    break;

                case "L":
                    if (speed <= 100 && duration <= 6) {
                        System.out.println("Moving Left...");
                        // For a left turn the left wheel must stay still and the right wheel must move
                        RightandLeft(command, 0, speed, duration, speed);
                    } else if (speed > 100 || speed <= 0) {
                        System.out.println("Invalid Speed");
                        flashLights();
                    } else {
                        System.out.println("Invalid Duration");
                        flashLights();
                    }
                    break;

                default:
                    System.out.println("Invalid Command");
                    flashLights();
                    break;
            }
        }

    }

    // Scans QR code and returns the decoded text to main in the form "F,x,y" where x<=100 and y<=6
    public static String ScanQR() {
        try {
            String decodedText = swiftBot.decodeQRImage(swiftBot.getQRImage());
            if (decodedText.isEmpty()) {
                System.out.println("QR code not detected... ");
            } else {
                System.out.println(decodedText);
                CommandLogHistory.add("S");
                return decodedText;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error in Scanning... \n");
            flashLights();
            //e.printStackTrace();
        }
        return "S 0 0";
    }

    // The string "command" is added to the F,B,R,L movements to be added to the CommandLog
    // Moves Bot in given direction for x speed for y seconds
    public static void ForwardandBackward(String command, int speed, int duration) {
        try {
            System.out.println("Going at " + speed + " speed for " + duration + " second(s)\n");
            swiftBot.move(speed, speed, duration * 1000);
            CommandLogHistory(command, String.valueOf(speed), String.valueOf(duration));
        } catch (IllegalArgumentException e) {
            System.out.println("Error in movement... \n");
            flashLights();
            //e.printStackTrace();
        }
    }

    // Turs bot in given direction at x speed for y duration (Display speed is added to keep the printed speed consistent without being confused between the left and right wheels)
    public static void RightandLeft(String command, int Lspeed, int Rspeed, int duration, int Displayspeed) {
        try {
            System.out.println("Going at " + Displayspeed + " speed for " + duration + " second(s)\n");
            swiftBot.move(Lspeed, Rspeed, duration * 1000);
            CommandLogHistory(command, String.valueOf(Displayspeed), String.valueOf(duration));
        } catch (IllegalArgumentException e) {
            System.out.println("Error in Turning... \n");
            flashLights();
            //e.printStackTrace();
        }
    }

    // Text that explains the purpose of the program and the inputs and rules to the user
    public static void Introduction() {
        System.out.println("\nThe purpose of this program is to guide the Swiftbot to navigate a space using commands provided via QR codes or text prompts.");
        System.out.println("Please enter your command in the form [command, speed, duration]");
        System.out.println("The commands consist of 'F' for Forward movement, 'B' for Backward movement, 'R' for a Right turn, and 'L' for a Left turn");
        System.out.println("Speeds cannot go above 100");
        System.out.println("Duration cannot go above 6 seconds");
        System.out.println("You can retrace your previous movements with the 'T' command followed by the amount of steps you want to retrace in the speed column");
        System.out.println("The command 'W' will write the log of previous commands into a text file");
        System.out.println("The command 'S' will scan a QR code\n");
    }

    // Appends the command, speed and duration to the ArrayList CommandLogHistory
    public static void CommandLogHistory(String command, String speed, String duration) {
        CommandLogHistory.add(command);
        CommandLogHistory.add(duration);
        CommandLogHistory.add(speed);
        System.out.println("Command Log History = " + CommandLogHistory);
    }

    // Replays through the commands in the CommandLogHistory
    public static void Traceback(int steps, int i) {
        System.out.println("Running Traceback... ");

        ArrayList<String> newCommandHistory = CommandLogHistory;

        // removes T's, W's and S's from the ArrayList
        if (newCommandHistory.contains("T")) {
            int occurrences = Collections.frequency(newCommandHistory, "T");
            while (occurrences > 0) {
                --occurrences;
                // Removes the number next to T too
                newCommandHistory.remove(newCommandHistory.indexOf("T") + 1);
                newCommandHistory.remove(newCommandHistory.indexOf("T"));
            }
        }
        if (newCommandHistory.contains("W")) {
            int occurrences = Collections.frequency(newCommandHistory, "W");
            while (occurrences > 0) {
                --occurrences;
                newCommandHistory.remove(newCommandHistory.indexOf("W"));
            }
        }
        if (newCommandHistory.contains("S")) {
            int occurrences = Collections.frequency(newCommandHistory, "S");
            while (occurrences > 0) {
                --occurrences;
                newCommandHistory.remove(newCommandHistory.indexOf("S"));
            }
        }

        // Where i is the index of the command to be executed, going from the frontmost command in the list
        i = newCommandHistory.size() - 3;

        System.out.println("New Command Log History = " + newCommandHistory);

        CommandLogHistory.add("T");
        CommandLogHistory.add(String.valueOf(steps / 3));

        while (steps > 0) {
            String command = newCommandHistory.get(i);
            // Gets the speed and duration that follows the command
            Integer duration = Integer.valueOf(newCommandHistory.get(i + 1));
            Integer speed = Integer.valueOf(newCommandHistory.get(i + 2));

            // Moves backwards 3 for the next command in the loop.
            i -= 3;
            // Reduces steps in increments of 3
            steps -= 3;

            switch (command) {
                case "F":
                    System.out.println("\nMoving Forwards... ");
                    ForwardandBackward(command, speed, duration);
                    break;

                case "B":
                    System.out.println("\nMoving Backwards... ");
                    ForwardandBackward(command, speed, duration);
                    break;

                case "R":
                    System.out.println("\nMoving Right... ");
                    RightandLeft(command, speed, 0, duration, speed);
                    break;

                case "L":
                    System.out.println("\nMoving Left... ");
                    RightandLeft(command, 0, speed, duration, speed);
                    break;

                // In case of failure to skip T command
                case "T":
                    System.out.println("\nSomehow a traceback was activated, Aborting");
                    System.out.println(CommandLogHistory);
                    System.exit(5);
                    break;

                // In case of no movement triggered
                default:
                    System.out.println("\nTraceback Failed");
                    break;
            }
        }
    }

    // Creates a file called CommandLogHistory.txt
    public static void CreateFile() {
        try {
            File LogHistory = new File("CommandLogHistory.txt");
            // If the file is new it prints 'File created', if not 'File already exists'
            if (LogHistory.createNewFile()) {
                System.out.println("\nFile created: " + LogHistory.getName());
                CommandLogHistory.add("W");
            } else {
                System.out.println("\nFile already exists.");
                CommandLogHistory.add("W");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            flashLights();
            e.printStackTrace();
        }
    }

    // Writes to the file CommandLogHistory.txt
    public static void WriteToFile(ArrayList<String> CommandLogHistory, LocalTime now) {
        try {
            FileWriter Writer = new FileWriter("CommandLogHistory.txt");
            // Writes the time and CmdLogHistory to a file
            Writer.write(now.toString() + "\n");
            Writer.write(CommandLogHistory.toString());
            Writer.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            flashLights();
            e.printStackTrace();
        }
    }

    // Flashes all of the lights on the swiftbot, can be used in all cases of an error
    public static void flashLights() {
        swiftBot.fillButtonLights();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        swiftBot.disableButtonLights();
    }
}
