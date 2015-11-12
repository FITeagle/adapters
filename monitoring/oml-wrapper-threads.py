#!/usr/bin/python

# This Python script is used to wrapp data from Zabbix and inject them as OML stream 
# to the semantic OML server, following RDF-based schemas
# 
# This script requires two libraries:
# 1- Zabbix API Python Library (zabbix_api.py) 
# 2- OML Python Library (oml4py), the modified version of v2.10.4 located at
# https://github.com/FITeagle/adapters/blob/master/monitoring/oml4py.py
#


import threading
import sqlite3
import sys
import logging
import logging.handlers
import subprocess
import oml4py

import math
from datetime import datetime
import ast
import exceptions
import cStringIO
import os
from zabbix_api import ZabbixAPI
import tempfile
import time
import pytz
from time import sleep 
import re

class omlclient (threading.Thread):
    def __init__(self, threadID, name, targeturi):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.target = targeturi
    def run(self):
        print "Starting " + self.name
        startOML(self.name, self.target)
        print self.name + " is terminated."

def init_logger(settings,name):
    logger=logging.getLogger(name)
    logfilename=settings['logger_filename']
    if(settings['logger_loglevel']=="DEBUG"):
        loglevel=logging.DEBUG
    elif settings['logger_loglevel']=="INFO":
        loglevel=logging.INFO
    elif settings['logger_loglevel']=="WARNING":
        loglevel=logging.WARNING
    else:
        loglevel=logging.ERROR
    
    logformatter=logging.Formatter(settings['logger_formatter'])
    logger.setLevel(loglevel)
    if(settings['logger_toconsole']=="1"):
        ch1 = logging.StreamHandler()
        ch1.setLevel(loglevel)
        ch1.setFormatter(logformatter)
        logger.addHandler(ch1)
    ch2 = logging.handlers.RotatingFileHandler(logfilename, maxBytes=int(settings['logger_maxBytes']), backupCount=int(settings['logger_backupCount']))
    ch2.setLevel(loglevel)
    ch2.setFormatter(logformatter)
    logger.addHandler(ch2) 
    return logger

def read_config(filename):
    try:
        f = open(filename, "r")
    except:
        logger.error("can not read file %s, script terminated" % (filename))
        sys.exit()
    try:
        dictionsry = {}
        for line in f:
            splitchar = '='
            kv = line.split(splitchar)
            if (len(kv)==2):
                dictionsry[kv[0]] = str(kv[1])[1:-2]
        return dictionsry
    except:
        logger.error("can not read file %s to a dictionary, format must be KEY=VALUE" % (filename))
        sys.exit()

def checkURL(url):
    if "http://" not in url :
        return url
    else :
        return url.replace("http://","")

def connect_sqlite() :
    try :
        ## create a SQLite database if not exist and connect to it
        con = sqlite3.connect(monitoring_settings['sqliteDB'])
    except Exception :
        logger.error("Cannot connect to SQLite3.")
    return con

def startOML(threadName, target):
    #-----------------------------------------------------------------------#
    omlInst = oml4py.OMLBase(monitoring_settings["appname"],monitoring_settings["domain"],monitoring_settings["sender"], target)

    omlInst.addmp("used_memory", "used_memory:double:{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:isMeasurementDataOf|omn-monitoring-metric:UsedMemory}{omn-monitoring-metric:UsedMemory|omn-monitoring:isMeasurementMetricOf|omn-domain-pc:PC}{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:hasMeasurementDataValue|%value%}{omn-monitoring-data:SimpleMeasurement|omn-monitoring:hasUnit|omn-monitoring-unit:Byte}{omn-monitoring-unit:Byte|omn-monitoring-unit:hasPrefix|omn-monitoring-unit:giga} timestamp:datetime:{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:hasTimestamp|%value%} physicalresource:string:{omn-domain-pc:PC|omn:hasURI|%value%} virtualresource:string:{omn-domain-pc:VM|omn:hasURI|%value%}{omn-domain-pc:VM|omn-lifecycle:childOf|omn-domain-pc:PC} ")
    omlInst.addmp("used_bandwidth", "used_bandwidth:double:{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:isMeasurementDataOf|omn-monitoring-metric:UsedBandwidth}{omn-monitoring-metric:UsedBandwidth|omn-monitoring:isMeasurementMetricOf|omn-domain-pc:PC}{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:hasMeasurementDataValue|%value%}{omn-monitoring-data:SimpleMeasurement|omn-monitoring:hasUnit|omn-monitoring-unit:bitpersecond}{omn-monitoring-unit:bitpersecond|omn-monitoring-unit:hasPrefix|omn-monitoring-unit:mega} timestamp:datetime:{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:hasTimestamp|%value%} physicalresource:string:{omn-domain-pc:PC|omn:hasURI|%value%} virtualresource:string:{omn-domain-pc:VM|omn:hasURI|%value%}{omn-domain-pc:VM|omn-lifecycle:childOf|omn-domain-pc:PC} ")
    omlInst.addmp("cpu_load", "cpu_load:double:{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:isMeasurementDataOf|omn-monitoring-metric:CPULoad}{omn-monitoring-metric:CPULoad|omn-monitoring:isMeasurementMetricOf|omn-domain-pc:PC}{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:hasMeasurementDataValue|%value%} timestamp:datetime:{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:hasTimestamp|%value%} physicalresource:string:{omn-domain-pc:PC|omn:hasURI|%value%} virtualresource:string:{omn-domain-pc:VM|omn:hasURI|%value%}{omn-domain-pc:VM|omn-lifecycle:childOf|omn-domain-pc:PC} ")
    omlInst.addmp("cpu_user", "cpu_user:double:{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:isMeasurementDataOf|omn-monitoring-metric:CPUuser}{omn-monitoring-metric:CPUuser|omn-monitoring:isMeasurementMetricOf|omn-domain-pc:PC}{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:hasMeasurementDataValue|%value%}{omn-monitoring-data:SimpleMeasurement|omn-monitoring:hasUnit|omn-monitoring-unit:percent} timestamp:datetime:{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:hasTimestamp|%value%} physicalresource:string:{omn-domain-pc:PC|omn:hasURI|%value%} virtualresource:string:{omn-domain-pc:VM|omn:hasURI|%value%}{omn-domain-pc:VM|omn-lifecycle:childOf|omn-domain-pc:PC} ")
    omlInst.addmp("cpu_system", "cpu_system:double:{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:isMeasurementDataOf|omn-monitoring-metric:CPUsystem}{omn-monitoring-metric:CPUsystem|omn-monitoring:isMeasurementMetricOf|omn-domain-pc:PC}{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:hasMeasurementDataValue|%value%}{omn-monitoring-data:SimpleMeasurement|omn-monitoring:hasUnit|omn-monitoring-unit:percent} timestamp:datetime:{omn-monitoring-data:SimpleMeasurement|omn-monitoring-data:hasTimestamp|%value%} physicalresource:string:{omn-domain-pc:PC|omn:hasURI|%value%} virtualresource:string:{omn-domain-pc:VM|omn:hasURI|%value%}{omn-domain-pc:VM|omn-lifecycle:childOf|omn-domain-pc:PC} ")

    #-----------------------------------------------------------------------#
    omlInst.start()

    while True :
        con = connect_sqlite()
        logger.debug("%s: Connecting to SQLite..." % threadName)

        with con :
                try :
                    cur = con.cursor()
                    cur.execute("select distinct(host_name), collector_uri, vm_uri from virtual_physical_map where collector_uri like \'%" + target + "%\'")
                    rows = cur.fetchall()
                    logger.debug("Fetching all host names from database...")
                    for row in rows :
                        print row[0], row[1], row[2]
                except:
                    logger.error("Error fetching data from SQLite. Try again in 30 secs...")

        if not rows :
            logger.error("No host name found. Exiting %s..." % threadName)
            global listOfCollectorURIs
            for elem in listOfCollectorURIs :
                if target in elem :
                    listOfCollectorURIs.remove(elem)
                    break
            break 

        else :        
            try:
                zabbix_server_uri = monitoring_settings['zabbixuri']
                zapi = ZabbixAPI(server=zabbix_server_uri, log_level=int(monitoring_settings['log_level']))
                zabbix_username = monitoring_settings['username']
                zabbix_password = monitoring_settings['password']
                zapi.login(zabbix_username,zabbix_password)

            except Exception as e:
                logger.error("can not open zabbix. Try again in 30 secs...")

            for row in rows :
                try:
                    hostid = zapi.host.get({"filter":{"name":row[0]},"output":"extend"}).pop()['hostid']

                    #metrics              
                    item = zapi.item.get({"output": "extend","hostids":hostid,"search":{"key_":"net.if.in[eth2]"}}).pop()
                    usedbandwidth = float(item['lastvalue']) / (1024)**2
                    usedbandwidth_ts = datetime.fromtimestamp(int(item['lastclock']),pytz.timezone("Europe/Berlin"))

                    item = zapi.item.get({"output": "extend","hostids":hostid,"search":{"name":"Used memory"}}).pop()
                    usedmemory = float(item['lastvalue']) / (1024)**3
                    usedmemory_ts = datetime.fromtimestamp(int(item['lastclock']),pytz.timezone("Europe/Berlin"))

                    item = zapi.item.get({"output": "extend","hostids":hostid,"search":{"key_":"system.cpu.load[percpu,avg5]"}}).pop()
                    cpuload = float(item['lastvalue'])
                    cpuload_ts = datetime.fromtimestamp(int(item['lastclock']),pytz.timezone("Europe/Berlin"))

                    item = zapi.item.get({"output": "extend","hostids":hostid,"search":{"key_":"system.cpu.util[,user]"}}).pop()
                    cpuuser = float(item['lastvalue'])
                    cpuuser_ts = datetime.fromtimestamp(int(item['lastclock']),pytz.timezone("Europe/Berlin"))

                    item = zapi.item.get({"output": "extend","hostids":hostid,"search":{"key_":"system.cpu.util[,system]"}}).pop()
                    cpusystem = float(item['lastvalue'])
                    cpusystem_ts = datetime.fromtimestamp(int(item['lastclock']),pytz.timezone("Europe/Berlin"))

                except Exception as e:
                    logger.error("cannot fetch data from Zabbix. Try again in 30 secs...")


                omlInst.inject("used_bandwidth", [usedbandwidth, usedbandwidth_ts, row[0], row[2]])
                omlInst.inject("used_memory", [usedmemory, usedmemory_ts, row[0], row[2]])
                omlInst.inject("cpu_load", [cpuload, cpuload_ts, row[0], row[2]])
                omlInst.inject("cpu_user", [cpuuser, cpuuser_ts, row[0], row[2]])
                omlInst.inject("cpu_system", [cpusystem, cpusystem_ts, row[0], row[2]])
        sleep(30)

    omlInst.close()   

########################################################################
#                           SCRIPT START                               #
########################################################################
monitoring_settings=read_config('monitoring-data.cfg')
logger=init_logger(monitoring_settings,'monitoringSQLresource.py')
logger.debug("monitoringSQLresource.py' has been started")
listOfCollectorURIs = []
threadCounter = 1

while True :
    con = connect_sqlite()
    logger.debug("MAIN THREAD: Connecting to SQLite...")
    with con :
            try :
                cur = con.cursor()
                cur.execute("select distinct(collector_uri) from virtual_physical_map")
                rows = cur.fetchall()
                logger.debug("Fetching collector URIs from database...")
                for row in rows :
                    print row[0]
            except:
                logger.error("Error fetching data from SQLite. Try again in 30 secs...")

    if not rows :
        logger.error("No collector URI found. Try again in 30 secs...")
        
    else :
        for row in rows :
            if row[0] not in listOfCollectorURIs :
                uri = checkURL(row[0])
                thread_name = "Thread-" + str(threadCounter)
                omlclient(threadCounter,thread_name,uri).start()
                listOfCollectorURIs.append(row[0])
                threadCounter += 1
                print "MAIN THREAD: waiting 30 secs before connecting to SQLite again..."
                
    sleep(30)



