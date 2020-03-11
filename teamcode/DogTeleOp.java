/*
* This particular OpMode just executes a basic Tank Drive Teleop for a four wheeled robot.
* It controls the Arms & Servos too.
* @ArminC
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name="Dog: TeleOp", group="OpMode")
public class DogTeleOp extends LinearOpMode {

    // Declare OpMode members.

    private ElapsedTime runtime = new ElapsedTime();
	
    /* Front */
    private DcMotor leftFrontDrive = null;
    private DcMotor rightFrontDrive = null;
	
    /* Rear */
    private DcMotor leftRearDrive = null;
    private DcMotor rightRearDrive = null;
	
    /* Arm */
    private DcMotor armWire = null;

    private Servo armLeftClaw;
    private Servo armRightClaw;
	
	double armLeftClawPosition, armRightClawPosition;
	double MIN_POSITION = 0, MAX_POSITION = 1;
	
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
        armLeftClaw = hardwareMap.get(Servo.class, "armLeftClaw");
        armRightClaw = hardwareMap.get(Servo.class, "armRightClaw");

        /* Park */
        leftParkClaw = hardwareMap.get(Servo.class, "leftParkClaw");
        rightParkClaw = hardwareMap.get(Servo.class, "rightParkClaw");

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
		

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
            if (gamepad1.y) {
                leftParkClaw.setPosition(0.5);
				rightParkClaw.setPosition(0.4);
            } else if (gamepad1.b) {
				leftParkClaw.setPosition(0);
                rightParkClaw.setPosition(1);
			}

            /* - User 2 - */

            // Arm Wire
            armPower = -gamepad2.right_stick_y;

            // Send calculated power to wire
            armWire.setPower(armPower);

            // Arm Claw
			if (gamepad2.x && armLeftClawPosition < MAX_POSITION && armRightClawPosition < MAX_POSITION) gripPosition = gripPosition + .01;

            if (gamepad2.x) {
                armLeftClaw.setPosition(1);
                armRightClaw.setPosition(0);
            } else if (gamepad2.a) {
                armLeftClaw.setPosition(0);
                armRightClaw.setPosition(1);
            }

            // Show the given datas.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Wheels:", "left (%.2f), right (%.2f)", leftPower, rightPower);
            telemetry.addData("Arm", "(%.2f)", armPower);
            telemetry.update();
        }
    }
}
