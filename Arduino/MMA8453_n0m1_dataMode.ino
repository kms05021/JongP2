//#include <I2C.h>
#include <MMA8451_n0m1.h>
#define BUFFERSIZE 50
#define BOUNDARY 0.14
#define DULATION 6000

MMA8451_n0m1 accel;
int value[BUFFERSIZE]={0};
int index=0;
long total=0;
long avg=0;
int inBound = 1;
int isCounting = 0;
long startTime=0;
long curTime=0;


void setup()
{
  Serial.begin(9600);
  accel.setI2CAddr(0x1C); //change your device address if necessary, default is 0x1C
  accel.dataMode(true, 2); //enable highRes 10bit, 2g range [2g,4g,8g]
  Serial.println("MMA8453_n0m1 library");
  Serial.println("XYZ Data Example");
  Serial.println("n0m1.com");

  for(int i=0; i < BUFFERSIZE; i++)
  {
    accel.update();
    value[i] = accel.x() + accel.y()+ accel.z();
    total = total + value[i];
  }
}

void loop()
{
    accel.update();
    int myAccel = accel.x() + accel.y()+ accel.z();
    
    Serial.print(" V: ");
    Serial.print(myAccel);
  
    total = total - value[index];
    value[index] = myAccel;
    total = total + value[index];
    index++;
    index = index % BUFFERSIZE;
    
    avg = total/BUFFERSIZE;
    Serial.print("  avg: ");
    Serial.println(avg);

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
          Serial.println("Warning!!");
        }
      }
      else  // 새로 범위내에 들어옴
      {
        isCounting = 1;
        startTime = accel.measure_time();
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
    

  
}
