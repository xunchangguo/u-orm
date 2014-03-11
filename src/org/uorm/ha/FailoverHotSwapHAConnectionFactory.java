/**
 * Copyright 2010-2016 the original author or authors.
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uorm.ha;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uorm.dao.common.ConnectionFactory;
import org.uorm.dao.common.DatasourceConfig;

/**
 * HA双机热备
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2014-2-12       郭训长            创建<br/>
 */
public class FailoverHotSwapHAConnectionFactory implements ConnectionFactory {
	private static final Logger logger = LoggerFactory.getLogger(FailoverHotSwapHAConnectionFactory.class);
	private HADataSourceDescriptor descriptor =null;
	private ConnectionFactory currentDataSource = null;
	private int recheckTimes = 3;
	private long recheckInterval = 5 * 1000;//ms
	private long detectingRequestTimeout = 15 * 1000;//ms
	private long monitorPeriod = 15 * 1000;//ms
	private String detectingSQL;
	
	private Map<ScheduledFuture<?>, ScheduledExecutorService> schedulerFutures = new ConcurrentHashMap<ScheduledFuture<?>, ScheduledExecutorService>();
	
	/**
	 * 
	 */
	public FailoverHotSwapHAConnectionFactory() {
		super();
	}

	/**
	 * @param descriptor
	 */
	public FailoverHotSwapHAConnectionFactory(HADataSourceDescriptor descriptor) {
		super();
		this.descriptor = descriptor;
		this.currentDataSource = this.descriptor.getMainDataSource();
	}
	
	/**
	 * init
	 */
	public void init() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		FailoverMonitorJob job = new FailoverMonitorJob(Executors.newFixedThreadPool(1));
		ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(job, 0,
                monitorPeriod, TimeUnit.MILLISECONDS);
		schedulerFutures.put(future, scheduler);
	}

	/**
	 * destroy
	 */
	public void destroy() {
		for (Map.Entry<ScheduledFuture<?>, ScheduledExecutorService> e : schedulerFutures
				.entrySet()) {
			ScheduledFuture<?> future = e.getKey();
			ScheduledExecutorService scheduler = e.getValue();
			future.cancel(true);
			shutdownExecutor(scheduler);
		}
	}
	
	private void shutdownExecutor(ExecutorService executor) {
        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (Exception ex) {
            logger.warn("interrupted when shutting down executor service.");
        }
    }
	
	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ConnectionFactory#openConnection()
	 */
	@Override
	public Connection openConnection() throws SQLException {
		return this.currentDataSource.openConnection();
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ConnectionFactory#getConfiguration()
	 */
	@Override
	public DatasourceConfig getConfiguration() {
		return currentDataSource == null ? descriptor.getMainDataSource().getConfiguration() : currentDataSource.getConfiguration();
	}

	/**
	 * @return the descriptor
	 */
	public HADataSourceDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * @param descriptor the descriptor to set
	 */
	public void setDescriptor(HADataSourceDescriptor descriptor) {
		this.descriptor = descriptor;
		this.currentDataSource = this.descriptor.getMainDataSource();
	}
	
	private ConnectionFactory getCurrentDetectorDataSource() {
		return currentDataSource == null ? descriptor.getMainDataSource() : currentDataSource;
	}

	/**
	 * @return the recheckTimes
	 */
	public int getRecheckTimes() {
		return recheckTimes;
	}

	/**
	 * @param recheckTimes the recheckTimes to set
	 */
	public void setRecheckTimes(int recheckTimes) {
		this.recheckTimes = recheckTimes;
	}

	/**
	 * @return the recheckInterval
	 */
	public long getRecheckInterval() {
		return recheckInterval;
	}

	/**
	 * @param recheckInterval the recheckInterval to set
	 */
	public void setRecheckInterval(long recheckInterval) {
		this.recheckInterval = recheckInterval;
	}

	/**
	 * @return the detectingRequestTimeout
	 */
	public long getDetectingRequestTimeout() {
		return detectingRequestTimeout;
	}

	/**
	 * @param detectingRequestTimeout the detectingRequestTimeout to set
	 */
	public void setDetectingRequestTimeout(long detectingRequestTimeout) {
		this.detectingRequestTimeout = detectingRequestTimeout;
	}

	/**
	 * @return the detectingSQL
	 */
	public String getDetectingSQL() {
		return detectingSQL;
	}

	/**
	 * @param detectingSQL the detectingSQL to set
	 */
	public void setDetectingSQL(String detectingSQL) {
		this.detectingSQL = detectingSQL;
	}

	/**
	 * @return the monitorPeriod
	 */
	public long getMonitorPeriod() {
		return monitorPeriod;
	}

	/**
	 * @param monitorPeriod the monitorPeriod to set
	 */
	public void setMonitorPeriod(long monitorPeriod) {
		this.monitorPeriod = monitorPeriod;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		destroy();
		super.finalize();
	}

	private class FailoverMonitorJob implements Runnable {
		private ExecutorService executor;
	    
	    public FailoverMonitorJob(ExecutorService es) {
	        this.executor = es;
	    }
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			Future<Integer> future = executor.submit(new Callable<Integer>() {

	            public Integer call() throws Exception {
	                Integer result = -1;

	                for (int i = 0; i < getRecheckTimes(); i++) {
	                    Connection conn = null;
	                    try {
	                        conn = getCurrentDetectorDataSource().openConnection();
	                        PreparedStatement pstmt = conn.prepareStatement(getDetectingSQL());
	                        pstmt.execute();
	                        if (pstmt != null) {
	                            pstmt.close();
	                        }
	                        result = 0;
	                        break;
	                    } catch (Exception e) {
	                        logger.warn("(" + (i + 1) + ") check with failure. sleep ("
	                                + getRecheckInterval() + ") for next round check.");
	                        try {
	                            TimeUnit.MILLISECONDS.sleep(getRecheckInterval());
	                        } catch (InterruptedException e1) {
	                            logger.warn("interrupted when waiting for next round rechecking.");
	                        }
	                        continue;
	                    } finally {
	                        if (conn != null) {
	                            try {
	                                conn.close();
	                            } catch (SQLException e) {
	                                logger.warn("failed to close checking connection:\n", e);
	                            }
	                        }
	                    }
	                }
	                return result;
	            }
	        });

	        try {
	            Integer result = future.get(getDetectingRequestTimeout(), TimeUnit.MILLISECONDS);
	            if (result == -1) {
	                doSwap();
	            }
	        } catch (InterruptedException e) {
	            logger.warn("interrupted when getting query result in FailoverMonitorJob.");
	        } catch (ExecutionException e) {
	            logger.warn("exception occured when checking failover status in FailoverMonitorJob");
	        } catch (TimeoutException e) {
	            logger.warn("exceed DetectingRequestTimeout threshold. Switch to standby data source.");
	            doSwap();
	        }
			
		}
		
	}

	public void doSwap() {
		synchronized(descriptor){
			ConnectionFactory masterDataSource = descriptor.getMainDataSource();
            if (masterDataSource == currentDataSource) {
                currentDataSource = descriptor.getStandbyDataSource();
            } else {
            	currentDataSource = descriptor.getMainDataSource();
            }
        }
	}
}
