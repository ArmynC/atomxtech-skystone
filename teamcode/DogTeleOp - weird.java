/*
 * 4 Wheels
 *
 * -User/Controller 1-
 * Wheels: (2 per) Left & Right JoyStick
 * Park: Button X & Y
 *
 * -User/Controller 2-
 * Arm Wire: Left JoyStick
 * Arm Grip: Left & Right Bumper
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Dog: TeleOp", group="OpMode")
public class DogTeleOp extends LinearOpMode {

    // Declare OpMode members.

    private ElapsedTime runtime = new ElapsedTime();

    /* Front */
    private DcMotor leftFrontDrive;
    private DcMotor rightFrontDrive;

    /* Rear */
    private DcMotor leftRearDrive;
    private DcMotor rightRearDrive;

    /* Arm */
    private DcMotor armWire;

    private Servo armLeftGrip;
    private Servo armRightGrip;

    private double armLeftGripPosition, armRightGripPosition;
    private double ZERO = 0, ONE = 1;

    /* Park */
    private Servo leftParkClaw;
    private Servo rightParkClaw;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration

        /* Front */
        leftFrontDrive = hardwareMap.get(DcMotor.class, "leftFrontDrive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rightFrontDrive");

        /* Rear */
        leftRearDrive = hardwareMap.get(DcMotor.class, "leftRearDrive");
        rightRearDrive = hardwareMap.get(DcMotor.class, "rightRearDrive");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightRearDrive.setDirection(DcMotor.Direction.REVERSE);

        /* Arm */
        // DcMotor
        armWire = hardwareMap.get(DcMotor.class, "armWire");
        // Servo
        armLeftGrip = hardwareMap.get(Servo.class, "armLeftGrip");
        armRightGrip = hardwareMap.get(Servo.class, "armRightGrip");

        /* Park */
        leftParkClaw = hardwareMap.get(Servo.class, "leftParkClaw");
        rightParkClaw = hardwareMap.get(Servo.class, "rightParkClaw");

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // Set Left & Right grip to 'full' open
        armLeftGripPosition = ONE; // ONE = MAX
        armRightGripPosition = ZERO; // ZERO = MAX

        // Run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            telemetry.addData("Status", "Running");
            telemetry.update();

            // Setup a variable for each drive wheel to save power level for telemetry
            double leftPower;
            double rightPower;
            double armPower;

            /* - User 1 - */

            // Tank Mode uses one stick to control each wheel.
            // - This requires no math, but it is hard to drive forward slowly and keep straight.
            leftPower = -gamepad1.right_stick_y;
            rightPower = -gamepad1.left_stick_y;

            // Send calculated power to wheels

            /* Left */
            leftFrontDrive.setPower(leftPower);
            leftRearDrive.setPower(leftPower);

            /* Right */
            rightFrontDrive.setPower(rightPower);
            rightRearDrive.setPower(rightPower);

            // Park
            if (gamepad1.y) { // Open
                leftParkClaw.setPosition(0.5);
                rightParkClaw.setPosition(0.4);
            } else if (gamepad1.b) { // Close
                leftParkClaw.setPosition(0);
                rightParkClaw.setPosition(1);
            }

            /* - User 2 - */

            // Arm Wire
            armPower = -gamepad2.right_stick_y;

            // Send calculated power to wire
            armWire.setPower(armPower);

            // Arm Grip
            if (gamepad2.right_bumper) { // Open
                if (armLeftGripPosition < ONE) {
                    if (armRightGripPosition > ZERO) {
                        armRightGripPosition = armRightGripPosition - .01;
                        armLeftGripPosition = armLeftGripPosition + .01;
                    } else {
                        armLeftGripPosition = armLeftGripPosition + .01;
                    }
                } else if (armRightGripPosition > ZERO) {
                    armRightGripPosition = armRightGripPosition - .01;
                }
            } else if (gamepad2.left_bumper) { // Close
                if (armLeftGripPosition > ZERO) {
                    if (armRightGripPosition < ONE) {
                        armRightGripPosition = armRightGripPosition + .01;
                        armLeftGripPosition = armLeftGripPosition - .01;
                    } else {
                        armLeftGripPosition = armLeftGripPosition - .01;
                    }
                } else if (armRightGripPosition > ONE) {
                    armRightGripPosition = armRightGripPosition + .01;
                }

                // Set the Servo position values as computed
                armLeftGrip.setPosition(Range.clip(armLeftGripPosition, ZERO, ONE));
                armRightGrip.setPosition(Range.clip(armRightGripPosition, ZERO, ONE));

                // Show the given datas.
                telemetry.addData("Status", "Run Time: " + runtime.toString());
                telemetry.addData("Wheels:", "left (%.2f), right (%.2f)", leftPower, rightPower);
                telemetry.addData("Arm:", "(%.2f)", armPower);
                telemetry.addData("Grip:", "position=" + gripPosition + "    actual=" + gripServo.getPosition());
                telemetry.addData("...via www.ArminC.Ga", "(%.2f)", armPower);
                telemetry.update();
            }
        }
    }
}