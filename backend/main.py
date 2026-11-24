from fastapi import FastAPI, Header, HTTPException
from pydantic import BaseModel
import os

app = FastAPI(title="ZE3ERY BOT API")

TOKEN = os.getenv("ZE3ERY_TOKEN", "ZE3ERY_DEMO_ONLY_CHANGE_ME")

bot_state = {
    "enabled": False,
    "mode": "DEMO",
    "symbol": "XAUUSD",
    "timeframe": "M5",
    "status": "STOPPED"
}

def check_token(token):
    if token != TOKEN:
        raise HTTPException(status_code=401, detail="Unauthorized")

class Control(BaseModel):
    enabled: bool

@app.get("/health")
def health():
    return {"server": "ZE3ERY BOT", "status": "online"}

@app.get("/app/status")
def app_status(x_device_token: str = Header(default="")):
    check_token(x_device_token)
    return bot_state

@app.post("/app/control")
def app_control(data: Control, x_device_token: str = Header(default="")):
    check_token(x_device_token)
    bot_state["enabled"] = data.enabled
    bot_state["status"] = "READY" if data.enabled else "STOPPED"
    return bot_state

@app.get("/mt5/poll")
def mt5_poll(x_device_token: str = Header(default="")):
    check_token(x_device_token)
    return {"enabled": bot_state["enabled"], "mode": "DEMO"}

@app.post("/mt5/status")
def mt5_status(data: dict, x_device_token: str = Header(default="")):
    check_token(x_device_token)
    return {"received": True}
