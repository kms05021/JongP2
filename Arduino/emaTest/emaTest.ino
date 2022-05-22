
#define BUFFERSIZE 5
#define TOLERANCE 1000
#define WARNINGTIME 5000
#include <math.h>

int rubberValues[BUFFERSIZE] = {0,};
int lastIndex = 0;
unsigned long startTime = 0;
unsigned long currTime = 0;
int isCounting = 0;
float sum=0;

//extra
float EMA_a = 0.8;
int EMA_S_pre = 0;
int EMA_S = 0;

void setup()
{
 Serial.begin(9600);

 EMA_S_pre = analogRead(A5); //초초초초초초초초초초기값값값값
/*
  for(int i = 0; i < BUFFERSIZE; i++)
  {
    int val = analogRead(A5);
    rubberValues[i] = val;
  }*/
}
void loop()
{
 int rubberVal = analogRead(A5);
 //Serial.print(" current: ");
 //Serial.println(rubberVal);

 ////extra
 EMA_S_pre = (EMA_a*rubberVal) + ((1-EMA_a)*EMA_S_pre);
 sum-=rubberValues[0];
 for(int i = 0; i < BUFFERSIZE - 1; i++){
    rubberValues[i] = rubberValues[i+1];
  }
 rubberValues[BUFFERSIZE-1] = EMA_S_pre;
 sum+=rubberValues[BUFFERSIZE-1];
 EMA_S = sum / BUFFERSIZE + 2;

 Serial.print("GDI ");
 Serial.println(EMA_S*10);

/* rubberValues[lastIndex] = rubberVal;
 lastIndex = (lastIndex+1) % BUFFERSIZE;
 int maxVal = -1;
 int minVal = 9999;
 for(int i = 0; i < BUFFERSIZE; i++)
 { 
  if(rubberValues[i] > maxVal)
    maxVal = rubberValues[i];
  else if (rubberValues[i] < minVal)
    minVal = rubberValues[i];
 }*/
 
///////SQR
/*
  int testNum = maxVal*maxVal - minVal*minVal;
  //testNum = testNum * testNum * testNumM;
  Serial.print(" test: ");
  Serial.print(testNum);
  Serial.print(" min: ");
  Serial.print(minVal);
  Serial.print(" max: ");
  Serial.println(maxVal);*/
  
////
/*
  int inBound = 0;

  //if(testNUM < TOLERANCE)
  if((float)minVal / maxVal > TOLERANCE)
    inBound = 1;
  else
    inBound = 0;

  if(isCounting)
  {
    if(inBound)
    {
      currTime = millis();
      if(currTime - startTime > WARNINGTIME)
      {
        //Serial.println("Warning!!");
      }
    }
    else
    {
        isCounting = 0;
        currTime = 0;
    }
  }
  else
  {
    if(inBound)
    {
      isCounting = 1;
      startTime = millis();  
    }
  }*/
}
