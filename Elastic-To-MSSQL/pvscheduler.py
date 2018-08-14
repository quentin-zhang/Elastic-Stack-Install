import pymysql.cursors
import datetime
import os
from elasticsearch import Elasticsearch
import pymssql
import yaml
# import pyodbc 

class PVScheduler:
    def __init__(self):
        pass
    def getYesterdayPV(self):
        f = open('uvsum.yaml', 'r')
        self.uvconf = yaml.load(f)
        self.yesterday = datetime.date.today() + datetime.timedelta(days=-1)
        self.today = datetime.date.today()
        self.yesterday_endStr = str(self.yesterday.year) + '-' + \
            f'{self.yesterday.month:02}' + '-' + f'{self.yesterday.day:02}'

        # Connect to the database
        es = Elasticsearch([
            {'host': self.uvconf['es']['host'], 'port': self.uvconf['es']['port']}
        ])

        bodystr = {
            "query": {
                "bool": {
                    "must_not": [
                        {
                            "match": {
                                "host": "id.inspur.com"
                            }
                        },
                        {
                            "match": {
                                "url": "*.css"
                            }
                        },
                        {
                            "match": {
                                "url": "*.js"
                            }
                        },
                        {
                            "match": {
                                "url": "*.png"
                            }
                        },
                        {
                            "match": {
                                "url": "*.jpg"
                            }
                        },
                        {
                            "match": {
                                "url": "*.jpeg"
                            }
                        }
                    ]
                }
            }
        }
        try:
            result = es.count(index='nginx:stable.alpine_1.entry.ecm-' + self.yesterday_endStr,
                              doc_type='doc', body=bodystr)
            self.rCount = result.get('count')
            print(self.rCount)
            print('success collect')
        except Exception as e:
            print('Error:', e)
        finally:
            print('collect end')

    def persistent(self):

        try:
            conn = pymssql.connect(server=self.uvconf['mssql']['server'],
                                   port=self.uvconf['mssql']['port'],
                                   user=self.uvconf['mssql']['user'],
                                   password=self.uvconf['mssql']['password'],
                                   database=self.uvconf['mssql']['database'])
            cursor = conn.cursor()
            cursor.execute("update UBAPVPerDay set ViewCount = %d where ProjectName = 'INSPURECM' and CreateTime = %s ",
                           (self.rCount, self.yesterday_endStr))
            conn.commit()
            conn.close()
            # cnxn = pyodbc.connect("Driver={SQL Server Native Client 11.0};"
            #           "Server=1.2.3.4;"
            #           "Database=db;"
            #           "UID=sa;PWD=123456")


            # cursor = cnxn.cursor()
            # cursor.execute("update UBAPVPerDay set ViewCount = ? where ProjectName = 'INSPURECM' and CreateTime = ? ",
            # (self.rCount, self.yesterday_endStr))
            # cnxn.commit()
            print('success update')
        except Exception as e:
            print('Error:', e)
        finally:
            print('send end')


scheduler = PVScheduler()
resp = scheduler.getYesterdayPV()
scheduler.persistent()
