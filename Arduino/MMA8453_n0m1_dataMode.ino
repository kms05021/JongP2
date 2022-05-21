#include <MMA8451_n0m1.h>

#define BUFFERSIZE 50 // 데이터를 보정할 표본 개수(이전 BUFFERSIZE 만큼의 데이터들을 합쳐서 평균값으로 보정함)
#define STDDEVIATION 100  // 무호흡 기준이 되는 표준편차
#define AVGBUFFERSIZE 300 // 평균을 구할 표본 개수
#define BREATHSTD 20  // 숨쉬는 상태의 기준이 되는 값 (표준편차와 비교함)

/*
호흡을 측정하는 과정은 보정과 표준편차 과정으로 나눈다.
먼저 최근 BUFFERSIZE 만큼의 가속도 값을 구해서 평균을 내어 가속도 값을 보정한다.
(기존 가속도 값은 심작 박동 등 외부 요인에 의한 간섭이 심하여 보정을 통해 일정한 가속도 값을 보장받는다.)
그 후 보정된 최근 가속도 값을 AVGBUFFERSIZE 만큼 수집하여 평균 및 표준편차를 구한다.
표준편차가 BREATHSTD 이하일 경우, 호흡이 중단된 상태로 판별한다.
(호흡 하는 동안에는 평균이 가속도 값의 중간쯤을 향하더라도 들숨과 날숨으로 인해 표준편차의 값은 크게 나타난다.
하지만 호흡이 멈추면 가속도 값은 현재 평균값에 수렴하는 상태가 되고 이 상태를 표준편차로 받아 호흡하는 상태를 확인할 수 있는것이다.)
*/

MMA8451_n0m1 accel; // 가속도 센서
int value[BUFFERSIZE]={0};  // 보정 배열
int avgArr[AVGBUFFERSIZE]={0,}; // 표준편차 배열
int index=0;  // 보정 인덱스
long total=0; // 보정에 사용할 총합 값
long avgForCalibration=0; // 보정값의 평균 (최종적 데이터 보정값)
int avgIndex=0; // 표준편차 인덱스
long avgForDeviation=0; // 표준편차 계산에 사용할 평균 값
long totalForDeviation=0; // 표준편차 계산에 사용할 총합 값
double deviation=0; // 표준편차 값 (최종 데이터 표준편차 값)

void initBuffer()
{
  for(int i=0; i < BUFFERSIZE; i++)
  {
    accel.update();
    value[i] = accel.x() + accel.y()+ accel.z();
    total = total + value[i];
  }
}

void updateAccel()
{
  // 호흡에 의한 가속도 데이터를 받아 최근 가속도 값들과 합하여 보정값을 구한다.
    accel.update();
    int myAccel = accel.x() + accel.y()+ accel.z();
  
    total = total - value[index];
    value[index] = myAccel;
    total = total + value[index];
    index++;
    index = index % BUFFERSIZE;
    
    avgForCalibration = total/BUFFERSIZE;
}

void initAvg()
{
  // 초기 호흡의 보정값을 구한다.
  initBuffer();

  // 보정된 초기 호흡값의 평균을 계산한다.
  for(int i=0;i<AVGBUFFERSIZE;i++)
  {
    updateAccel();
    /////////////////////////
    avgArr[i] = avgForCalibration;
    totalForDeviation = totalForDeviation + avgArr[i];
  }
}

void setup()
{
  Serial.begin(9600);
  accel.setI2CAddr(0x1C); //change your device address if necessary, default is 0x1C
  accel.dataMode(true, 2); //enable highRes 10bit, 2g range [2g,4g,8g]
  Serial.println("MMA8453_n0m1 library");
  Serial.println("XYZ Data Example");
  Serial.println("n0m1.com");

  // 초기 호흡을 세팅한다.
  initAvg();
}

void loop()
{
  ////////////////// 가속도 데이터 값받기 및 보정 ////////////////
  
    updateAccel();

    ////////////////// 평균 구하기 //////////////////
    
    totalForDeviation = totalForDeviation - avgArr[avgIndex];
    avgArr[avgIndex] = avgForCalibration;
    totalForDeviation = totalForDeviation + avgArr[avgIndex];
    avgIndex++;
    avgIndex = avgIndex % AVGBUFFERSIZE;

    avgForDeviation = totalForDeviation / AVGBUFFERSIZE;
    
    ///////////////////// 표준편차 구하기 ///////////
    deviation = 0.0;
    for(int i=0;i<AVGBUFFERSIZE;i++)
    {
      deviation = deviation + (double)pow(float(avgArr[i]-avgForDeviation),(float)2);
    }
    deviation = deviation / AVGBUFFERSIZE;
    deviation = sqrt(deviation);
    
    ////////// Output ///////////////
    Serial.print("  avg: ");
    Serial.print(avgForDeviation);
    Serial.print("  deviation: ");
    Serial.print(deviation);
    Serial.print("  data: ");
    Serial.println(avgForCalibration);

  
}
