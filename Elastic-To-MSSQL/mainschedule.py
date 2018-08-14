import schedule, time
import pvscheduler

def job():
    print("I'm working...")
    scheduler = pvscheduler.PVScheduler()
    scheduler.getYesterdayPV()
    scheduler.persistent()
    print("OK...")


schedule.every().day.at("7:00").do(job)
while True:
    schedule.run_pending()
    time.sleep(1)