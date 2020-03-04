/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.XboxController; 
import edu.wpi.first.wpilibj.GenericHID.Hand; 
//import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.cameraserver.CameraServer ; 
import edu.wpi.first.wpilibj.Encoder; 




/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private Spark m_Intake = new Spark(RobotMap.INTAKE_MOTOR);
  private Spark m_Arm = new Spark(RobotMap. ARM_MOTOR); 
  private Encoder leftEncoder;
  private Encoder rightEncoder; 
  private Spark m_Climber = new Spark (RobotMap.CLIMBER_MOTOR) ; 
  private Spark m_Spinner = new Spark (RobotMap.SPINNER_MOTOR) ;  //added 2/27/2020 LMC
  private boolean autoDone;
  private double rightStart; 
  private double leftStart;
  private double leftPower, rightPower ; 
  XboxController xbox; 
  XboxController ltech;  
  Spark m_frontLeft = new Spark(RobotMap.FRONT_LEFT_MOTOR);
  Spark m_rearLeft = new Spark(RobotMap.REAR_LEFT_MOTOR);
  SpeedControllerGroup m_left = new SpeedControllerGroup(m_frontLeft, m_rearLeft);
  
  Spark m_frontRight = new Spark(RobotMap.FRONT_RIGHT_MOTOR);
  Spark m_rearRight = new Spark(RobotMap.REAR_RIGHT_MOTOR);
  SpeedControllerGroup m_right = new SpeedControllerGroup (m_frontRight, m_rearRight);

  private final DifferentialDrive m_robotDrive
      = new DifferentialDrive(m_left, m_right);
  private final Timer m_timer = new Timer();



  private static final String DRIVE_ONLY = "Drive Only";
  private static final String DRIVE_PUSH = "Drive and Push";
  private static final String DRIVE_DROP = "Drive and Drop";
  private static final String DRIVE_TURN = "Drive and Turn"; 




  
  

  //PWMVictorSPX m_left = new PWMVictorSPX(0) ;
    //PWMVictorSPX m_right = new PWMVictorSPX(1) ;  ### LMC: Motors left and right 

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
  //  m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
  //  m_chooser.addOption("My Auto", kCustomAuto);
    m_chooser.setDefaultOption("Drive Only", DRIVE_ONLY);
    m_chooser.addOption("Drive Push", DRIVE_PUSH);
    m_chooser.addOption("Drive Drop", DRIVE_DROP);
  SmartDashboard.putData("Auto choices", m_chooser);
   // m_right.setInverted(true); //### LMC: invert the right motor on test bed ###m_right.setInverted(true); //### LMC: invert the right motor on test bed ###
    xbox  =  new XboxController (1);
    ltech = new XboxController (0);
    CameraServer.getInstance().startAutomaticCapture(RobotMap.CAMERA_SERVER_1);
    CameraServer.getInstance().startAutomaticCapture(RobotMap.CAMERA_SERVER_2);
    leftEncoder = new Encoder  (RobotMap.LEFT_ENCODER_A, RobotMap.LEFT_ENCODER_B, true); //reverses left direction
    leftEncoder.setDistancePerPulse((3.141592*6)/360);
  //  leftEncoder.reset();
    rightEncoder = new Encoder (RobotMap.RIGHT_ENDOCER_A, RobotMap.RIGHT_ENCODER_B) ; 
    rightEncoder.setDistancePerPulse((3.141592*6)/360); 
  //  rightEncoder.reset();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
   /* SmartDashboard.putNumber("Left Joystick  X" , xbox.getX(Hand.kLeft)) ; 
    SmartDashboard.putNumber("Left Joystick Y" , xbox.getY(Hand.kLeft)) ;
    SmartDashboard.putNumber("Right Joystick Y" , xbox.getY(Hand.kRight)) ; 
    SmartDashboard.putNumber("Right Joystick X" , xbox.getX(Hand.kRight)) ;    
    SmartDashboard.putNumber("Left Trigger" , xbox.getTriggerAxis(Hand.kLeft)); 
    SmartDashboard.putNumber("Right Trigger" , xbox.getTriggerAxis(Hand.kRight));  
   */
     
    SmartDashboard.putNumber("L En Value", leftEncoder.getRaw()) ; 
    SmartDashboard.putNumber("L En Distance", leftEncoder.getDistance()) ;  
    SmartDashboard.putNumber("R En Value",  rightEncoder.getRaw());
    SmartDashboard.putNumber ("R En Distance", rightEncoder.getDistance()); 

  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_timer.reset();
    m_timer.start();
    leftPower = 0.50;
    m_left.set(leftPower);
    rightPower = 0.50;
    m_right.set(rightPower);
    autoDone=false;

    //reset the encoders and get the starting positions
    // [[starting position necessary after all?? seem to be resetting to 0 now]]
    leftEncoder.reset();
    rightEncoder.reset();
    rightStart = rightEncoder.getDistance(); 
    leftStart = leftEncoder.getDistance();
    System.out.print("RS" + rightStart + " LS" + leftStart + "\n");

    //select the auto mode
    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);

    //position arm to game position
    m_Arm.set(-.5);
    Timer.delay(.5);
    m_Arm.set(.5);
    Timer.delay(.5);
    
  }
 public void driveStraight () {
  /****** 
  double error = leftdistance - rightdistance;
  double kP = 0.005; 
  leftPower -= kP*error;
  rightPower += kP*error; 
  m_left.set(leftPower);
  m_right.set(rightPower);
  ******/
 }


  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {

    double rightdistance = Math.abs(rightEncoder.getDistance() - rightStart);
    double leftdistance = Math.abs(leftEncoder.getDistance() - leftStart);
    System.out.println("LE" + leftEncoder.getDistance() + " LD" + leftdistance + " RE" + rightEncoder.getDistance() + " RD" + rightdistance + "\n" );

    switch (m_autoSelected) {
      case DRIVE_PUSH:    //kCustomAuto:
      if (rightdistance  < 120) { 
        m_robotDrive.arcadeDrive(.5,0);
      } else {
        m_robotDrive.stopMotor();
       /* m_Arm.set(-.5);
        Timer.delay(2);
        m_Arm.set(.5);
        Timer.delay(2); */
        m_Intake.set(-0.5); 
      }
       
        break;

      case DRIVE_DROP:
      if (rightdistance < 12) {
        m_robotDrive.arcadeDrive(.4 , 0 ); 
        } else {
          m_robotDrive.stopMotor(); // stop robot
          m_Intake.set(-0.5);
        }
        break;
      
      case DRIVE_TURN:
      if (rightdistance  - rightStart < 24) {
        m_robotDrive.arcadeDrive(.5,0);
      } else if (rightdistance < 30) {
          m_robotDrive.arcadeDrive(.5,.2);
      } else if (rightdistance < 45) {
          m_robotDrive.arcadeDrive(.5, 0 ); 
      } else m_robotDrive.stopMotor(); 
      break;


      case DRIVE_ONLY:     //kDefaultAuto:
      default:
 /*     
      if (!autoDone) {
        if (m_timer.hasPeriodPassed(0.125)) {
//          driveStraight();
            double error = leftdistance - rightdistance;
            System.out.print(leftdistance + "R" + rightdistance + "E" + error + "LP" + leftPower + "RP" + rightPower);
            double kP = 0.005; 
            leftPower -= kP*error;
            rightPower += kP*error; 
            m_left.set(leftPower);
            m_right.set(rightPower);
        }
        if (leftdistance > 24.0 || rightdistance > 24.0) {
            m_left.stopMotor();
            m_right.stopMotor(); 
            autoDone = true; 
          }
        }
   */   
      if (rightdistance < 12) { 
        m_robotDrive.arcadeDrive(.5 , 0); // drive forwards half speed
        } else {
          m_robotDrive.stopMotor(); // stop robot
        }
        
      break;
    }
  }


  /******************************************************************/
  /******************************************************************/
  /* code to get the color string:
  1) do we need to get it all the time or just when we want to spin?
  2) can we put a note on the driver station?
  3) should it be in a function and return the value  OR
               be in a function and actually do the spinning?
  4) or just go ahead and put all in the teleopPeriodic?

  String gameData;
  gameData = DriverStation.getInstance().getGameSpecificMessage();
  if (gameData.length() > 0)
  {
    switch (gameData.charAt(0))
    {
      case 'B':
        //blue case code
        break;
      case 'G':
        //green case code
        break;
      case 'R':
        //red case code
        break;
      case 'Y':
        //yellow case code
        break;
      default:
        //this is corrupt data
        break;
    }
  } else {
      // code for no data received yet
  }


  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    m_robotDrive.arcadeDrive( -1* xbox.getY(Hand.kLeft),  xbox.getX(Hand.kLeft));

    //INTAKE: LTECH BUMPER

    if (ltech.getBumperPressed (Hand.kLeft)) {
      m_Intake.set(.8) ; 
    }
    if (ltech.getBumperReleased (Hand.kLeft)) {
     m_Intake.set(0) ; 
    }
    if (ltech.getBumperPressed (Hand.kRight)) {
      m_Intake.set(-.8) ;
    }
    if (ltech.getBumperReleased (Hand.kRight)) {
      m_Intake.set(0) ; 
    }

    //CLIMBER: XBOX A, Y, X

    if (xbox.getAButtonPressed ()) {
      m_Climber.set(-0.60) ;
    }
    if (xbox.getAButtonReleased ()) {
      m_Climber.set(0) ;
    }
    if (xbox.getYButtonPressed ()) {
      m_Climber.set(1) ; 
    }
    if (xbox.getYButtonReleased ()) {
      m_Climber.set(0) ; 
    }
    if (xbox.getXButtonPressed ()) {
      m_Climber.set(0.6) ; 
    }
    if (xbox.getXButtonReleased ()) {
      m_Climber.set(0) ; 
    }
   
    //ARM: LTECH Y AND A
    // ***NOTE for some reason, BButton is responding to the actual A button *** //
    //Logitech Y and A buttons for raising and lowering arm  
    
    if (ltech.getYButtonPressed()) {
      m_Arm.set(0.4);
    }
    if (ltech.getYButtonReleased()) {
      m_Arm.set(0); 
    }
    // When originally had A button code, A did not respond on controller...the Bcode is responding to A button
    if (ltech.getBButtonPressed()) {
      m_Arm.set(-0.5);
    }
    if (ltech.getBButtonReleased()) {       
      m_Arm.set(0);  
    }

    //SPINNER: LTECH X AND B   . . .  X for program, B for operator control
    // ***NOTE equally annoying, AButton is responding for B Button 

    if (ltech.getXButtonPressed()) { //added 2/27/2020  
      m_Spinner.set(0.15) ;
      Timer.delay(2);
      m_Spinner.set(0);
    }
    /** 
    if (ltech.getXButtonReleased()) {
      m_Spinner.set(0) ; 
    */
    if (ltech.getAButtonPressed()) {
      m_Spinner.set(0.15) ; 
    }
    if (ltech.getAButtonReleased()) {
      m_Spinner.set(0); 
    }


    // Couldn't get Logitech trigger button to work the same as xbox  so we swtiched to Y and A button , left code in case we wanted to go back to xbox controller
    /*
    if (ltech.getTriggerAxis (Hand.kLeft) >= 0.9) {
      System.out.println("TURN ON");
      m_Arm.set(0.5) ;
    }
    if (ltech.getTriggerAxis (Hand.kLeft) < 0.1 && ltech.getTriggerAxis (Hand.kRight) < 0.1) {
      m_Arm.set(0);
      System.out.println("TURN OFF");
    }
   System.out.println(ltech.getTriggerAxis(Hand.kLeft));
   System.out.println(ltech.getTriggerAxis(Hand.kRight));

    if (ltech.getTriggerAxis (Hand.kRight) >= 0.9) {
      m_Arm.set(-0.5) ;
    }
    */
    
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
