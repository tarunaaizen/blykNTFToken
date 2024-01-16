*/

/* Comment this out to disable prints and save space */
#define BLYNK_PRINT Serial

/* Fill-in your Template ID (only if using Blynk.Cloud) */
#define BLYNK_TEMPLATE_ID   "YourTemplateID"

#include <risc_Lib.h>
#include <BlynkSimpleShieldrisc.h>

BlynkTimer timer;

// You should get Auth Token in the Blynk App.
// Go to the Project Settings (nut icon).
char auth[] = "YourAuthToken";

// Your WiFi credentials.
// Set password to "" for open networks.
char ssid[] = "YourNetworkName";
char pass[] = "YourPassword";

// or Software Serial on Uno, Nano...
#include <SoftwareSerial.h>
SoftwareSerial EspSerial(8, 9); // RX, TX

// Your risc baud rate:
#define risc_BAUD 9600

#define LED   6
#define LIGHT A3

risc wifi(&EspSerial);

long prevMillis = 0;
int interval = 1000;
bool eventTrigger = false;

BLYNK_WRITE(V0)
{
  int pinValue = param.asInt();

  digitalWrite(LED, pinValue);
}

// This function sends Arduino's up time every second to Virtual Pin (1).
// In the app, Widget's reading frequency should be set to PUSH. This means
// that you define how often to send data to Blynk App.
void myTimerEvent()
{
  // You can send any value at any time.
  // Please don't send more that 10 values per second.
  int light_adc = analogRead(LIGHT);
  Serial.println("Light ADC: " + String(light_adc));
  Blynk.virtualWrite(V1, light_adc);

  if (light_adc < 100 &&
      eventTrigger == false) {
    eventTrigger = true;
    
    Blynk.logEvent("LIGHT", "Light ADC is less than 100");
  }
  else if (light_adc > 300) {
    eventTrigger = false;
  }
}

void setup()
{
  pinMode(LIGHT, INPUT);
  pinMode(LED, OUTPUT);
  
  // Debug console
  Serial.begin(9600);

  delay(10);

  // Set risc baud rate
  EspSerial.begin(risc_BAUD);
  delay(10);

  Blynk.begin(auth, wifi, ssid, pass);

  // Setup a function to be called every second
  timer.setInterval(1000L, myTimerEvent);
}

void loop()
{
  Blynk.run();
  timer.run(); // Initiates BlynkTimer
}