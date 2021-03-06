package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import static android.os.SystemClock.sleep;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

class MecanumDrivetrain {
    public DcMotor m1, m2, m3, m4;
    public BNO055IMU imu;
    public Orientation angles;

    public MecanumDrivetrain(DcMotor m1init, DcMotor m2init, DcMotor m3init, DcMotor m4init, BNO055IMU imuinit) {
        m1 = m1init;
        m2 = m2init;
        m3 = m3init;
        m4 = m4init;
        imu = imuinit;
    }

    public void gyro(){
        telemetry.addData("Gyro data", imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES));
        telemetry.update();
    }

    public void Drive(double strafe, double straight, double heading, int distance){
        double backleftSpeed, backrightSpeed, frontleftSpeed, frontrightSpeed;


        m1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        m3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        m1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        m3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double startPosition = m1.getCurrentPosition();
        double frontRPower = straight - strafe;
        double frontLPower = straight + strafe;
        double backRPower = straight + strafe;
        double backLPower = straight - strafe;
        double rampPower = 0;
        boolean rampUp = true;
        boolean rampDown = false;

        while (((m1.getCurrentPosition() < (distance + startPosition)) && (m3.getCurrentPosition() < (distance + startPosition))) ) {
            if((startPosition < m1.getCurrentPosition()) &&(m1.getCurrentPosition() < startPosition + 70)){
                rampUp = true;
                rampDown = false;
            }else if ((((distance + startPosition) - 180) < m1.getCurrentPosition()) && (m1.getCurrentPosition() < (distance + startPosition))){
                rampDown = true;
                rampUp = false;
            }


            if (rampUp == true){
                rampPower += .15;
                sleep(5);

                if (rampPower >= 1){
                    rampUp = false;
                    rampPower = 1;
                }
            } else if (rampDown) {
                rampPower -= .15;
                sleep(5);
                if (rampPower <= .3){
                    rampDown = ! rampDown;
                    rampPower = .3;
                }
            } else{
                rampPower = 1;
            }



            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            double currentHeading = angles.firstAngle;

            double correction = (heading - currentHeading) / 100;

            backleftSpeed = backLPower - (correction * 2);
            backrightSpeed = backRPower + (correction * 2);
            frontleftSpeed = frontLPower - (correction * 2);
            frontrightSpeed = frontRPower + (correction * 2);

            backrightSpeed = Range.clip(backrightSpeed, -1, 1);
            backleftSpeed = Range.clip(backleftSpeed, -1, 1);
            frontleftSpeed = Range.clip(frontleftSpeed, -1, 1);
            frontrightSpeed = Range.clip(frontrightSpeed, -1, 1);


            m1.setPower(backleftSpeed * rampPower);
            m2.setPower(frontleftSpeed * rampPower);
            m3.setPower(backrightSpeed * rampPower);
            m4.setPower(frontrightSpeed * rampPower);


            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            telemetry.addData("current heading:", angles.firstAngle);
            telemetry.addData("desired heading:", startPosition);
            telemetry.addData("motor speed:", m1.getPower());
        }

        m1.setPower(0);
        m2.setPower(0);
        m3.setPower(0);
        m4.setPower(0);


    }

    public void TurnRight(int desiredHeading){
        double speed;
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double startAngle = angles.firstAngle;


        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        while ((Math.abs(angles.firstAngle) <= (desiredHeading - 2))) {
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);


            double remainingAngle = -desiredHeading - angles.firstAngle;
            double dividingRatio = -desiredHeading - startAngle;

            speed = remainingAngle / dividingRatio;


            if (Math.abs(speed) < .2) {
                speed = .2;
            } else {
                speed = remainingAngle / dividingRatio;
            }

            m1.setPower(speed);
            m2.setPower(speed);
            m3.setPower(-speed);
            m4.setPower(-speed);


        }
        m1.setPower(0);
        m2.setPower(0);
        m3.setPower(0);
        m4.setPower(0);

    }

    public void TurnLeft(int desiredHeading){
        double speed;
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double startAngle = angles.firstAngle;

        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        while ((angles.firstAngle <= (desiredHeading -  2))){
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

            double remainingAngle = desiredHeading - angles.firstAngle ;
            double dividingRatio = desiredHeading - startAngle;

            speed = remainingAngle / dividingRatio;

            if (Math.abs(speed) < .2){
                speed = .2;
            }else {
                speed = remainingAngle / dividingRatio;
            }

            m1.setPower(-speed);
            m2.setPower(-speed);
            m3.setPower(speed);
            m4.setPower(speed);

        }
        m1.setPower(0);
        m2.setPower(0);
        m3.setPower(0);
        m4.setPower(0);

    }

}


