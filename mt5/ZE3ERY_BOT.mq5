//+------------------------------------------------------------------+
//|                    ZE3ERY BOT                                    |
//|              XAUUSD - M5 - DEMO DEVELOPMENT                     |
//+------------------------------------------------------------------+
#property strict
#property version "1.00"

input string TradeSymbol = "XAUUSD";
input ENUM_TIMEFRAMES TimeFrame = PERIOD_M5;
input double RiskPercent = 0.5;
input double DailyLossLimit = 2.0;
input bool EnableTrading = false;

int FastEMA, SlowEMA, RSIHandle, ATRHandle;
datetime LastBar = 0;

int OnInit()
{
   FastEMA = iMA(TradeSymbol, TimeFrame, 20, 0, MODE_EMA, PRICE_CLOSE);
   SlowEMA = iMA(TradeSymbol, TimeFrame, 50, 0, MODE_EMA, PRICE_CLOSE);
   RSIHandle = iRSI(TradeSymbol, TimeFrame, 14, PRICE_CLOSE);
   ATRHandle = iATR(TradeSymbol, TimeFrame, 14);

   if(FastEMA == INVALID_HANDLE ||
      SlowEMA == INVALID_HANDLE ||
      RSIHandle == INVALID_HANDLE ||
      ATRHandle == INVALID_HANDLE)
   {
      Print("ZE3ERY BOT: Indicator initialization failed");
      return INIT_FAILED;
   }

   Print("ZE3ERY BOT initialized - SIGNAL MODE ONLY");
   Print("Trading enabled: ", EnableTrading);
   return INIT_SUCCEEDED;
}

void OnTick()
{
   datetime currentBar = iTime(TradeSymbol, TimeFrame, 0);

   if(currentBar == 0 || currentBar == LastBar)
      return;

   LastBar = currentBar;

   double fast[], slow[], rsi[], atr[];

   ArraySetAsSeries(fast, true);
   ArraySetAsSeries(slow, true);
   ArraySetAsSeries(rsi, true);
   ArraySetAsSeries(atr, true);

   if(CopyBuffer(FastEMA, 0, 1, 2, fast) < 2) return;
   if(CopyBuffer(SlowEMA, 0, 1, 2, slow) < 2) return;
   if(CopyBuffer(RSIHandle, 0, 1, 2, rsi) < 2) return;
   if(CopyBuffer(ATRHandle, 0, 1, 2, atr) < 2) return;

   double closePrice = iClose(TradeSymbol, TimeFrame, 1);

   if(fast[0] > slow[0] && rsi[0] > 50 && closePrice > fast[0])
      Print("ZE3ERY BOT: BUY signal detected");

   else if(fast[0] < slow[0] && rsi[0] < 50 && closePrice < fast[0])
      Print("ZE3ERY BOT: SELL signal detected");

   else
      Print("ZE3ERY BOT: No signal");

   Print("ATR: ", atr[0]);
   Print("Risk setting: ", RiskPercent, "%");
   Print("Daily loss limit setting: ", DailyLossLimit, "%");
}

void OnDeinit(const int reason)
{
   IndicatorRelease(FastEMA);
   IndicatorRelease(SlowEMA);
   IndicatorRelease(RSIHandle);
   IndicatorRelease(ATRHandle);

   Print("ZE3ERY BOT stopped");
}
