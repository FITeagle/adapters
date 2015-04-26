import sqlite3
import sys
import logging
import logging.handlers
import subprocess
import oml4py

import time
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

def connect_sqlite() :
	try :
		## create a SQLite database if not exist and connect to it
		con = sqlite3.connect(monitoring_settings['sqliteDB'])
	except Exception :
		logger.error("Cannot connect to SQLite3.")
		sys.exit()
	return con

########################################################################
#							SCRIPT START							   #
########################################################################
monitoring_settings=read_config('/Users/andisadewi/fiteagle/adapters/monitoring/monitoring-data.cfg')
logger=init_logger(monitoring_settings,'monitoringSQL.py')
logger.debug("monitoringSQL.py' has been started")

con = connect_sqlite()
logger.debug("Connecting to SQLite...")

with con :
		try :
			cur = con.cursor()
			cur.execute("select distinct(host_name), collector_uri, vm_uri from virtual_physical_map")
			rows = cur.fetchall()
			logger.debug("Fetching all host names from database...")
			for row in rows :
				print row[0], row[1], row[2]
		except:
			logger.error("Error fetching data from SQLite.")
			sys.exit()

if not rows :
	logger.error("No host name found. Exiting...")
	sys.exit()

#-----------------------------------------------------------------------#
omlInst = oml4py.OMLBase(monitoring_settings["appname"],monitoring_settings["domain"],monitoring_settings["sender"], "tcp:130.149.22.139")
#omlInst = oml4py.OMLBase(monitoring_settings["appname"],monitoring_settings["domain"],monitoring_settings["sender"], "tcp:" + row[1])
#omlInst.addmp("availability", "availability:int32:{omn:Resource|MOFID:hasMeasurementMetric|MOFID:Availability}{MOFID:Availability|MOFID:hasMetricAttributes|MOFID:AvailabilityMeasurement}{MOFID:AvailabilityMeasurement|MOFID:MeasurementDataValue|%value%} timestamp:datetime:{omn:Resource|MOFID:hasMeasurementMetric|MOFID:Availability}{MOFID:Availability|MOFID:hasMetricAttributes|MOFID:AvailabilityMeasurement}{MOFID:AvailabilityMeasurement|MOFID:timestamp|%value%} hostname:string:{omn:Resource|omn-resource:hasName|%value%} ")
#omlInst.addmp("total_memory", "total_memory:double:{omn:Resource|MOFID:hasMeasurementMetric|MOFID:TotalMemory}{MOFID:TotalMemory|MOFID:hasMetricAttributes|MOFID:TotalMemoryMeasurement}{MOFID:TotalMemoryMeasurement|MOFID:MeasurementDataValue|%value%}{MOFID:TotalMemoryMeasurement|MOFID:isMeasuredIn|MOFIU:Byte}{MOFID:TotalMemoryMeasurement|MOFIU:hasPrefix|MOFIU:giga} timestamp:datetime:{omn:Resource|MOFID:hasMeasurementMetric|MOFID:TotalMemory}{MOFID:TotalMemory|MOFID:hasMetricAttributes|MOFID:TotalMemoryMeasurement}{MOFID:TotalMemoryMeasurement|MOFID:timestamp|%value%} hostname:string:{omn:Resource|omn-resource:hasName|%value%} ")
#omlInst.addmp("available_memory", "available_memory:double:{omn:Resource|MOFID:hasMeasurementMetric|MOFID:AvailableMemory}{MOFID:AvailableMemory|MOFID:hasMetricAttributes|MOFID:AvailableMemoryMeasurement}{MOFID:AvailableMemoryMeasurement|MOFID:MeasurementDataValue|%value%}{MOFID:AvailableMemoryMeasurement|MOFID:isMeasuredIn|MOFIU:Byte}{MOFID:AvailableMemoryMeasurement|MOFIU:hasPrefix|MOFIU:giga} timestamp:datetime:{omn:Resource|MOFID:hasMeasurementMetric|MOFID:AvailableMemory}{MOFID:AvailableMemory|MOFID:hasMetricAttributes|MOFID:AvailableMemoryMeasurement}{MOFID:AvailableMemoryMeasurement|MOFID:timestamp|%value%} hostname:string:{omn:Resource|omn-resource:hasName|%value%} ")
#omlInst.addmp("used_memory", "used_memory:double:{omn:Resource|MOFID:hasMeasurementMetric|MOFID:UsedMemory}{MOFID:UsedMemory|MOFID:hasMetricAttributes|MOFID:UsedMemoryMeasurement}{MOFID:UsedMemoryMeasurement|MOFID:MeasurementDataValue|%value%}{MOFID:UsedMemoryMeasurement|MOFID:isMeasuredIn|MOFIU:Byte}{MOFID:UsedMemoryMeasurement|MOFIU:hasPrefix|MOFIU:giga} timestamp:datetime:{omn:Resource|MOFID:hasMeasurementMetric|MOFID:UsedMemory}{MOFID:UsedMemory|MOFID:hasMetricAttributes|MOFID:UsedMemoryMeasurement}{MOFID:UsedMemoryMeasurement|MOFID:timestamp|%value%} hostname:string:{omn:Resource|omn-resource:hasName|%value%} ")

#omlInst.addmp("total_memory", "total_memory:double:{MOFID:Measurement|MOFID:isMeasurementOf|MOFID:TotalMemory}{MOFID:TotalMemory|MOFID:isMeasurementMetricOf|omn:Resource}{MOFID:TotalMemory|MOFID:hasMetricAttributes|MOFID:TotalMemoryMeasurement}{MOFID:TotalMemoryMeasurement|MOFID:MeasurementDataValue|%value%}{MOFID:TotalMemoryMeasurement|MOFID:isMeasuredIn|MOFIU:Byte}{MOFID:TotalMemoryMeasurement|MOFIU:hasPrefix|MOFIU:giga} timestamp:datetime:{MOFID:Measurement|MOFID:isMeasurementOf|MOFID:TotalMemory}{MOFID:TotalMemory|MOFID:isMeasurementMetricOf|omn:Resource}{MOFID:TotalMemory|MOFID:hasMetricAttributes|MOFID:TotalMemoryMeasurement}{MOFID:TotalMemoryMeasurement|MOFID:timestamp|%value%} physicalresource:string:{omn-domain-pc:PC|rdfs:label|%value%} virtualresource:string:{omn-domain-pc:VM|rdfs:label|%value%} ")
#omlInst.addmp("available_memory", "available_memory:double:{MOFID:Measurement|MOFID:isMeasurementOf|MOFID:AvailableMemory}{MOFID:AvailableMemory|MOFID:isMeasurementMetricOf|omn:Resource}{MOFID:AvailableMemory|MOFID:hasMetricAttributes|MOFID:AvailableMemoryMeasurement}{MOFID:AvailableMemoryMeasurement|MOFID:MeasurementDataValue|%value%}{MOFID:AvailableMemoryMeasurement|MOFID:isMeasuredIn|MOFIU:Byte}{MOFID:AvailableMemoryMeasurement|MOFIU:hasPrefix|MOFIU:giga} timestamp:datetime:{MOFID:Measurement|MOFID:isMeasurementOf|MOFID:AvailableMemory}{MOFID:AvailableMemory|MOFID:isMeasurementMetricOf|omn:Resource}{MOFID:AvailableMemory|MOFID:hasMetricAttributes|MOFID:AvailableMemoryMeasurement}{MOFID:AvailableMemoryMeasurement|MOFID:timestamp|%value%} physicalresource:string:{omn-domain-pc:PC|rdfs:label|%value%} virtualresource:string:{omn-domain-pc:VM|rdfs:label|%value%} ")
#omlInst.addmp("used_memory", "used_memory:double:{MOFID:Measurement|MOFID:isMeasurementOf|MOFID:UsedMemory}{MOFID:UsedMemory|MOFID:isMeasurementMetricOf|omn-domain-pc:PC}{MOFID:UsedMemory|MOFID:hasMetricAttributes|MOFID:UsedMemoryMeasurement}{MOFID:UsedMemoryMeasurement|MOFID:MeasurementDataValue|%value%}{MOFID:UsedMemoryMeasurement|MOFID:isMeasuredIn|MOFIU:Byte}{MOFID:UsedMemoryMeasurement|MOFIU:hasPrefix|MOFIU:giga} timestamp:datetime:{MOFID:Measurement|MOFID:isMeasurementOf|MOFID:UsedMemory}{MOFID:UsedMemory|MOFID:isMeasurementMetricOf|omn-domain-pc:PC}{MOFID:UsedMemory|MOFID:hasMetricAttributes|MOFID:UsedMemoryMeasurement}{MOFID:UsedMemoryMeasurement|MOFID:timestamp|%value%} physicalresource:string:{omn-domain-pc:PC|rdfs:label|%value%} virtualresource:string:{omn-domain-pc:VM|rdfs:label|%value%} ")
#omlInst.addmp("availability", "availability:int32:{MOFID:Measurement|MOFID:isMeasurementOf|MOFID:Availability}{MOFID:Availability|MOFID:isMeasurementMetricOf|omn:Resource}{MOFID:Availability|MOFID:hasMetricAttributes|MOFID:AvailabilityMeasurement}{MOFID:AvailabilityMeasurement|MOFID:MeasurementDataValue|%value%} timestamp:datetime:{MOFID:Measurement|MOFID:isMeasurementOf|MOFID:Availability}{MOFID:Availability|MOFID:isMeasurementMetricOf|omn:Resource}{MOFID:Availability|MOFID:hasMetricAttributes|MOFID:AvailabilityMeasurement}{MOFID:AvailabilityMeasurement|MOFID:timestamp|%value%} physicalresource:string:{omn-domain-pc:PC|rdfs:label|%value%} virtualresource:string:{omn-domain-pc:VM|rdfs:label|%value%} ")
omlInst.addmp("used_bandwidth", "used_bandwidth:double:{MOFID:Measurement|MOFID:isMeasurementOf|MOFID:UsedBandwidth}{MOFID:UsedBandwidth|MOFID:isMeasurementMetricOf|omn-resource:Interface}{omn-resource:Interface|omn-resource:isInterfaceOf|omn-domain-pc:PC}{MOFID:UsedBandwidth|MOFID:hasMetricAttributes|MOFID:UsedBandwidthMeasurement}{MOFID:UsedBandwidthMeasurement|MOFID:MeasurementDataValue|%value%}{MOFID:UsedBandwidthMeasurement|MOFIU:hasPrefix|MOFIU:mega}{MOFID:UsedBandwidthMeasurement|MOFID:defaultUnit|MOFID:bitspersec} timestamp:datetime:{MOFID:Measurement|MOFID:isMeasurementOf|MOFID:UsedBandwidth}{MOFID:UsedBandwidth|MOFID:isMeasurementMetricOf|omn-resource:Interface}{omn-resource:Interface|omn-resource:isInterfaceOf|omn-domain-pc:PC}{MOFID:UsedBandwidth|MOFID:hasMetricAttributes|MOFID:UsedBandwidthMeasurement}{MOFID:UsedBandwidthMeasurement|MOFID:timestamp|%value%} physicalresource:string:{omn-domain-pc:PC|rdfs:label|%value%} virtualresource:string:{omn-domain-pc:VM|rdfs:label|%value%} ")

#-----------------------------------------------------------------------#
omlInst.start()

try:
	zabbix_server_uri = monitoring_settings['zabbixuri']
	zapi = ZabbixAPI(server=zabbix_server_uri, log_level=int(monitoring_settings['log_level']))
	zabbix_username = monitoring_settings['username']
	zabbix_password = monitoring_settings['password']
	zapi.login(zabbix_username,zabbix_password)

except Exception as e:
	print e
	logger.error("can not open zabbix.")
	sys.exit()

tz=pytz.timezone("Europe/Berlin")
aware_dt=tz.localize(datetime.now())
current=aware_dt.isoformat() #datetime.now().isoformat() #time.time()

for row in rows :
	try:
		hostid = zapi.host.get({"filter":{"name":row[0]},"output":"extend"}).pop()['hostid']

		#metrics
		totalmem = float(zapi.item.get({"output": "extend","hostids":hostid,"search":{"name":"Total memory"}}).pop()['lastvalue']) / (1024)**3
		usedmem = float(zapi.item.get({"output": "extend","hostids":hostid,"search":{"name":"Used memory"}}).pop()['lastvalue']) / (1024)**3
		availmem = float(zapi.item.get({"output": "extend","hostids":hostid,"search":{"name":"Available memory"}}).pop()['lastvalue']) / (1024)**3
		usedbandwidth = float(zapi.item.get({"output": "extend","hostids":hostid,"search":{"key_":"net.if.in[eth2]"}}).pop()['lastvalue']) / (1024)**2
	
	except Exception as e:
		print e
		logger.error("cannot fetch data from Zabbix.")
		sys.exit()

	#omlInst.inject("total_memory", [row[0], totalmem, current])
	#omlInst.inject("used_memory", [row[0], usedmem, current])
	#omlInst.inject("available_memory", [row[0], availmem, current])

	#omlInst.inject("total_memory", [totalmem, current, row[0], row[2]])
	#omlInst.inject("used_memory", [usedmem, current, row[0], row[2]])
	#omlInst.inject("available_memory", [availmem, current, row[0], row[2]])
	omlInst.inject("used_bandwidth", [usedbandwidth, current, row[0], row[2]])

omlInst.close()		










