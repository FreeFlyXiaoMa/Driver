package com.junjun.drivertest.iohandler;

import com.junjun.bean.BasicDriverBean;
import com.junjun.bean.CodeBean;
import com.junjun.bean.CustomBean;
import com.junjun.bean.LoginResultBean;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class MyIoHandler implements IoHandler {

    @Override
    public void sessionCreated(IoSession ioSession) throws Exception {

    }

    @Override
    public void sessionOpened(IoSession ioSession) throws Exception {

    }

    @Override
    public void sessionClosed(IoSession ioSession) throws Exception {

    }

    @Override
    public void sessionIdle(IoSession ioSession, IdleStatus idleStatus) throws Exception {

    }

    @Override
    public void exceptionCaught(IoSession ioSession, Throwable throwable) throws Exception {

    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (message.getClass() == CodeBean.class) {
            CodeBean code = (CodeBean) message;
            reCode.showCode(code);
        } else if (message.getClass() == LoginResultBean.class) {
            LoginResultBean result = (LoginResultBean)message;
            reLoginResult.showLoginResult(result);
        } else if (message.getClass() == CustomBean.class) {
            CustomBean custom = (CustomBean)message;
            reCustom.showCustom(custom);
        } else if (message.getClass() == BasicDriverBean.class) {
            BasicDriverBean driver = (BasicDriverBean)message;
            reDriver.deleteCustom(driver);
        }
    }

    @Override
    public void messageSent(IoSession ioSession, Object o) throws Exception {
    }

    @Override
    public void inputClosed(IoSession ioSession) throws Exception {

    }

    //接口们
    private ReCode reCode;
    public void setReCodeListener(ReCode r) {
        this.reCode = r;
    }

    private ReLoginResult reLoginResult;
    public void setReLoginResultListener(ReLoginResult r){
        this.reLoginResult = r;
    }

    private ReCustom reCustom;
    public void setReCustomListener(ReCustom r){
        this.reCustom = r;
    }

    private ReDriver reDriver;
    public void setReDriverListener(ReDriver r){
        this.reDriver = r;
    }
}
