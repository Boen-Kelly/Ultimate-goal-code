package org.firstinspires.ftc.teamcode.Red_Autonomous;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorImpl;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@Autonomous(name = "Red side tray park front side", group = "Red")
@Disabled
public class Red_side_tray_front extends LinearOpMode {
    private  ElapsedTime runtime = new ElapsedTime();
    private DcMotor backLeftWheel,backRightWheel,frontLeftWheel,frontRightWheel,linearLift,linearLift2;
    private Servo CLAW,trayServoL,trayServoR;
    BNO055IMU imu;
    Orientation angles;
    DigitalChannel Touch;
    DistanceSensor Distance;

    //ColorSensor color;

    @Override public void runOpMode() throws InterruptedException{

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.calibrationDataFile = "BNO055IMUcalibration.json";

        imu = hardwareMap.get(BNO055IMU.class,"imu");
        backLeftWheel =  hardwareMap.get(DcMotor.class, "Back_left_wheel");
        backRightWheel = hardwareMap.get(DcMotor.class,"Back_right_wheel");
        frontLeftWheel = hardwareMap.get(DcMotor.class,"Front_left_wheel");
        frontRightWheel = hardwareMap.get(DcMotor.class,"Front_right_wheel");
        CLAW = hardwareMap.servo.get("CLAW");
        linearLift = hardwareMap.get(DcMotor.class,"linearLift");
        linearLift2 = hardwareMap.get(DcMotor.class,"linearLift2");
        trayServoL = hardwareMap.servo.get("trayServoL");
        trayServoR = hardwareMap.servo.get("trayServoR");
        Touch = hardwareMap.get(DigitalChannel.class, "Touch");
        Distance = hardwareMap.get(DistanceSensor.class,"Distance");
        imu.initialize(parameters);

        backRightWheel.setDirection(DcMotor.Direction.REVERSE);
        frontRightWheel.setDirection(DcMotor.Direction.REVERSE);
        backLeftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearLift2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearLift2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        trayServoL.setPosition(0);
        trayServoR.setPosition(1);
        CLAW.setPosition(0);
        Touch.setMode(DigitalChannel.Mode.INPUT);

        while(!imu.isGyroCalibrated() && !isStopRequested()){

        }

        backLeftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        linearLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        linearLift2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //color = hardwareMap.colorSensor.get("color");

        telemetry.addData("Status:","initialized");
        telemetry.update();
        waitForStart();

        Lift(1120,1);
        Drive(350,.7,0);
        Drop(0,.5);
        CLAW.setPosition(1);
        sleep(500);
        Lift(840,1);
        DrivewTouch(-.7,0,4,0);

        Drive(600,0,.4);

        DrivewDistance(3,.7,0);
        Drive(32,.4,0);

        trayServoL.setPosition(1);
        trayServoR.setPosition(0);
        sleep(500);

        DrivewTouch(-.7,0,4,0);

        trayServoL.setPosition(0);
        trayServoR.setPosition(1);
        Drive(32,.5,0);
        Drive(650,0,-.7);
        Drop(0,.5);
        Drive(300,.7,0);
        Drive(280,0,.7);
        Drive(80,.7,0);
        Drive(560,0,-.7);
        CLAW.setPosition(0);
        sleep(1000);
        runtime.reset();

        telemetry.addData("Linear lift position", linearLift.getCurrentPosition());
        telemetry.addData("Time", runtime.time());
        telemetry.update();




    }
    //This is a function or in EV3 software, a myBlock

    public void Drive(int distance, double straight,double strafe)throws InterruptedException{

        double backleftSpeed,backrightSpeed,frontleftSpeed,frontrightSpeed;


        backLeftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double target = angles.firstAngle;
        double startPosition = backLeftWheel.getCurrentPosition();
        double frontRPower = straight - strafe;
        double frontLPower = straight + strafe;
        double backRPower = straight + strafe;
        double backLPower  = straight - strafe;

        while (((backLeftWheel.getCurrentPosition() < (distance + startPosition)) && (backRightWheel.getCurrentPosition() < (distance + startPosition))) && !isStopRequested()){
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            double currentHeading = angles.firstAngle;

            double correction = (target - currentHeading)/100;

            backleftSpeed = backLPower - (correction * 2);
            backrightSpeed = backRPower + (correction * 2);
            frontleftSpeed = frontLPower - (correction * 2);
            frontrightSpeed = frontRPower + (correction * 2);

            backrightSpeed = Range.clip (backrightSpeed,-1,1);
            backleftSpeed = Range.clip (backleftSpeed,-1,1);
            frontleftSpeed = Range.clip (frontleftSpeed,-1,1);
            frontrightSpeed = Range.clip(frontrightSpeed,-1,1);


            backLeftWheel.setPower(backleftSpeed);
            frontLeftWheel.setPower(frontleftSpeed);
            backRightWheel.setPower(backrightSpeed);
            frontRightWheel.setPower(frontrightSpeed);


            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            telemetry.addData("current heading:", angles.firstAngle);
            telemetry.addData("desired heading:", startPosition);
        }

        backLeftWheel.setPower(0);
        frontLeftWheel.setPower(0);
        backRightWheel.setPower(0);
        frontRightWheel.setPower(0);


    }

    public void Lift (int Height, double power)throws InterruptedException{
        while((linearLift.getCurrentPosition() >= -Height) && !isStopRequested()){
            linearLift.setPower(-power);
            linearLift2.setPower(-power);
        }
        linearLift.setPower(0);
        linearLift2.setPower(0);
    }

    public void Drop (int Height, double power)throws InterruptedException{
        while((linearLift.getCurrentPosition() <= -Height ) && !isStopRequested()){
            linearLift.setPower(power);
            linearLift2.setPower(power);
        }
        linearLift.setPower(0);
        linearLift2.setPower(0);
    }

    public void DrivewTouch(double straight,double strafe, double time, int target)throws InterruptedException{

        double backleftSpeed,backrightSpeed,frontleftSpeed,frontrightSpeed;


        backLeftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double startPosition = backLeftWheel.getCurrentPosition();
        double frontRPower = straight - strafe;
        double frontLPower = straight + strafe;
        double backRPower = straight + strafe;
        double backLPower  = straight - strafe;
        double timeLimit = runtime.time() + time;

        while (((Touch.getState() == true) && !isStopRequested()) && runtime.time() < timeLimit){
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            double currentHeading = angles.firstAngle;

            double correction = (target - currentHeading)/100;

            backleftSpeed = backLPower - (correction * 2);
            backrightSpeed = backRPower + (correction * 2);
            frontleftSpeed = frontLPower - (correction * 2);
            frontrightSpeed = frontRPower + (correction * 2);

            backrightSpeed = Range.clip (backrightSpeed,-1,1);
            backleftSpeed = Range.clip (backleftSpeed,-1,1);
            frontleftSpeed = Range.clip (frontleftSpeed,-1,1);
            frontrightSpeed = Range.clip(frontrightSpeed,-1,1);


            backLeftWheel.setPower(backleftSpeed);
            frontLeftWheel.setPower(frontleftSpeed);
            backRightWheel.setPower(backrightSpeed);
            frontRightWheel.setPower(frontrightSpeed);


            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            telemetry.addData("current heading:", angles.firstAngle);
            telemetry.addData("desired heading:", startPosition);
        }

        backLeftWheel.setPower(0);
        frontLeftWheel.setPower(0);
        backRightWheel.setPower(0);
        frontRightWheel.setPower(0);


    }

    public void DrivewDistance(double range, double straight,double strafe)throws InterruptedException{

        double backleftSpeed,backrightSpeed,frontleftSpeed,frontrightSpeed;


        backLeftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double target = angles.firstAngle;
        double startPosition = backLeftWheel.getCurrentPosition();
        double frontRPower = straight - strafe;
        double frontLPower = straight + strafe;
        double backRPower = straight + strafe;
        double backLPower  = straight - strafe;

        while (!(Distance.getDistance(DistanceUnit.INCH) < range) && !isStopRequested()){
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            double currentHeading = angles.firstAngle;

            double correction = (target - currentHeading)/100;

            backleftSpeed = backLPower - (correction * 2);
            backrightSpeed = backRPower + (correction * 2);
            frontleftSpeed = frontLPower - (correction * 2);
            frontrightSpeed = frontRPower + (correction * 2);

            backrightSpeed = Range.clip (backrightSpeed,-1,1);
            backleftSpeed = Range.clip (backleftSpeed,-1,1);
            frontleftSpeed = Range.clip (frontleftSpeed,-1,1);
            frontrightSpeed = Range.clip(frontrightSpeed,-1,1);


            backLeftWheel.setPower(backleftSpeed);
            frontLeftWheel.setPower(frontleftSpeed);
            backRightWheel.setPower(backrightSpeed);
            frontRightWheel.setPower(frontrightSpeed);


            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            telemetry.addData("current heading:", angles.firstAngle);
            telemetry.addData("desired heading:", startPosition);
        }

        backLeftWheel.setPower(0);
        frontLeftWheel.setPower(0);
        backRightWheel.setPower(0);
        frontRightWheel.setPower(0);


    }
}
