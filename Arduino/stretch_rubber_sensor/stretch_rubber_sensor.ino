
#define BUFFERSIZE 50
#define TOLERANCE 0.98
#define WARNINGTIME 10000

int rubberValues[BUFFERSIZE] = {0,};
int lastIndex = 0;
unsigned long startTime = 0;
unsigned long currTime = 0;
int isCounting = 0;


void setup()
{
 Serial.begin(9600);

  for(int i = 0; i < BUFFERSIZE; i++)
  {
    int val = analogRead(A0);
    rubberValues[i] = val;
  }
}
void loop()
{
 int rubberVal = analogRead(A0);
 Serial.print(" current: ");
 Serial.println(rubberVal);

 rubberValues[lastIndex] = rubberVal;
 lastIndex = (lastIndex+1) % BUFFERSIZE;
 int maxVal = -1;
 int minVal = 9999;
 for(int i = 0; i < BUFFERSIZE; i++)
 { 
  if(rubberValues[i] > maxVal)
    maxVal = rubberValues[i];
  else if (rubberValues[i] < minVal)
    minVal = rubberValues[i];
 }
  int inBound = 0;
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
        Serial.println("Warning!!");
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
  }
}
