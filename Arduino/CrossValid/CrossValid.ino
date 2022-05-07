#include <SoftwareSerial.h>
#include <I2C.h>
#include <MMA8451_n0m1.h>
// Accel MACRO
#define BUFFERSIZE 50
#define BOUNDARY 0.095
#define DULATION 6000
// Wind MACRO
#define Alert 10000
#define Warning 15000
#define Emergency 20000

// Bluetooth global var
const int pinTx = 7;  // 블루투스 TX 연결 핀 번호
const int pinRx = 6;  // 블루투스 RX 연결 핀 번호
SoftwareSerial   bluetooth( pinTx, pinRx );

// Accel global var
MMA8451_n0m1 accel;
int value[BUFFERSIZE]={0};
int index=0;
unsigned long total=0;
unsigned long avg=0;
int inBound = 1;
int isCounting = 0;
int validAccel = 0;
int lowMovement = 0;

// Wind global var
const float zeroWindAdjustment =  .2; // negative numbers yield smaller wind speeds and vice versa.
int TMP_Therm_ADunits;  //temp termistor value from wind sensor
float RV_Wind_ADunits;    //RV output from wind sensor 
float RV_Wind_Volts;
unsigned long lastMillis;
int TempCtimes100;
float zeroWind_ADunits;
float zeroWind_volts;
float WindSpeed_MPH;

int inBoundW = 1;
int isCountingW = 0;
int validVibe = 0;
int validAlarm = 0;
int validCall = 0;
int noWind = 0;


// Delta Time var
unsigned long startTime=0;
unsigned long curTime=0;

 
void  setup()
{
  bluetooth.begin(9600);  // 블루투스 통신 초기화 (속도= 9600 bps)
  Serial.begin(9600);

  // Accel Init
  accel.setI2CAddr(0x1C); //change your device address if necessary, default is 0x1C
  accel.dataMode(true, 2); //enable highRes 10bit, 2g range [2g,4g,8g]
  Serial.println("MMA8453_n0m1 library");
  Serial.println("XYZ Data Example");
  Serial.println("n0m1.com");

  for(int i=0; i < BUFFERSIZE; i++)
  {
    accel.update();
    value[i] = accel.x() + accel.y() + accel.z();
    total = total + value[i];
  }

}
 
 
void  loop()
{
  accel.update();
  int myAccel = accel.x() + accel.y()+ accel.z();
  
  // 블루투스 수신 
  if ( bluetooth.available() ) 
  {
    Serial.print((char)bluetooth.read());
  }
  else
  {
    delay( 10 );
  }
  // 블루투스 송신
  if (Serial.available()) { 
    //시리얼 모니터에서 입력된 값을 송신
    char toSend = (char)Serial.read();
    bluetooth.print(toSend);
  }
  TMP_Therm_ADunits = analogRead(A0);
  RV_Wind_ADunits = analogRead(A1);
  RV_Wind_Volts = (RV_Wind_ADunits *  0.0048828125);
  TempCtimes100 = (0.005 *((float)TMP_Therm_ADunits * (float)TMP_Therm_ADunits)) - (16.862 * (float)TMP_Therm_ADunits) + 9075.4;  
  zeroWind_ADunits = -0.0006*((float)TMP_Therm_ADunits * (float)TMP_Therm_ADunits) + 1.0727 * (float)TMP_Therm_ADunits + 47.172;  //  13.0C  553  482.39
  zeroWind_volts = (zeroWind_ADunits * 0.0048828125) - zeroWindAdjustment; 
  WindSpeed_MPH =  pow(((RV_Wind_Volts - zeroWind_volts) /.2300) , 2.7265);
  //Serial.print("  TMP volts ");
  //Serial.print(TMP_Therm_ADunits * 0.0048828125);
  //Serial.print(" RV volts ");
  //Serial.print((float)RV_Wind_Volts);
  //Serial.print("\t  TempC*100 ");
  //Serial.print(TempCtimes100 );
  //Serial.print("   ZeroWind volts ");
  //Serial.print(zeroWind_volts);
  //Serial.print("   WindSpeed MPH ");
  //Serial.println((float)WindSpeed_MPH);
  //bluetooth.println((float)WindSpeed_MPH);

  // Wind Check
  if(WindSpeed_MPH < 1 || isnan(WindSpeed_MPH))
  {
    inBoundW = 0;
  }
  else
  {
    inBoundW = 1;
  }

  if(!inBoundW)
    {
      if(isCountingW)
      {
        curTime = millis();
        if(curTime - startTime >= Alert && curTime - startTime < Warning)
        {
          if(validVibe==0)
          {
            bluetooth.println("LEVEL1");
            Serial.println("Vibration!!");
            validVibe=1;
            noWind = 1;
          }

        }
        else if(curTime - startTime >= Warning && curTime - startTime < Emergency)
        {
          if(validAlarm==0)
          {
            bluetooth.println("LEVEL2");
            Serial.println("Alarm!!");
            validAlarm=1;
            noWind = 1;
          }
        }
        else if(curTime - startTime >= Emergency)
        {
          if(validCall==0)
          {
            bluetooth.println("LEVEL3");
            Serial.println("Call!!");
            validCall=1;
            noWind = 1;
          }
        }
      }
      else
      {
        isCountingW = 1;
        startTime = millis();
      }
    }
    else
    {
      if(isCountingW)
      {
        bluetooth.println("OK");
        isCountingW = 0;
        startTime = curTime = 0;
        validVibe = 0;
        validAlarm = 0;
        validCall = 0;
        noWind = 0;
      }
    }
    
    // Accel Check
    total = total - value[index];
    value[index] = myAccel;
    total = total + value[index];
    index++;
    index = index % BUFFERSIZE;
    
    avg = total/BUFFERSIZE;
    //Serial.print("  avg: ");
    //Serial.println(avg);

    if(myAccel > avg * (1 - BOUNDARY) && myAccel < avg * (1 + BOUNDARY))
    {
      inBound = 0;
    }
    else
    {
      inBound = 1;  
    }

    /*inBound -> avg +- 30내 : 0
                아니면 : 1

    단위 시간 동안 계속 0 -> 뒤짐 아니면 살아있음*/



    if(!inBound) // 범위내
    {
      if(isCounting) // 지금 카운트 중
      {
        curTime = accel.measure_time();
        if(curTime - startTime >= DULATION)
        {
            if(validAccel==0)
            {
              //bluetooth.println("Low Movement!!");
              lowMovement=1;
              validAccel=1;
            }
        }
      }
      else  // 새로 범위내에 들어옴
      {
        isCounting = 1;
        startTime = accel.measure_time();
        lowMovement=0;
        validAccel=0;
      }
    }
    else
    {
      if(isCounting)
      {
        isCounting = 0;
        startTime = curTime = 0;
      }
    }

    // Cross Check
    if(lowMovement & noWind == 1)
    {
       //bluetooth.println("Die....");
    }
}
