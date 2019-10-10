运维手册

测试环境

研发环境----服务器地址

- 39.105.209.79(172.17.137.5)
- root / J3@9s#ldJ0jI5Nh8qk!di0oilLsj34#

http://39.105.209.79:8888/PuyangPlatform 测试平台环境地址

http://39.105.209.79:8080 测试环境门户地址

测试环境项目所在位置：/root/puyang

puyang-platform项目上线需要本地打好war包，上传到Tomcat的webapps目录下进行发布。

puyang-quickcash，puyang-usermenu，puyang-wap，puyang-web，puyang-weixin测试环境发布的命令：

首先要确定分支git branch，然后 git pull,   ./build.sh,  ./cleanup.sh,  ./startup.sh。

bigscreen-proxy 

生产环境

生产环境---服务器地址

                 服务器IP                	                  地址                  	    用户名/密码     	          备注          
  10.217.22.75/（公网IP：111.7.74.22:8092）	http://111.7.74.22:8092/PuyangPlatform	root/Pyzhjr12#$	       银行端和政府端        
   10.217.22.76/（公网IP：111.7.74.29:80） 	     wx.pyjrfw.com/wap.pyjrfw.com     	root/Pyzhjr12#$	app前端和展业助手前端，nginx负载均衡
              10.217.22.78            	                                      	root/Pyzhjr12#$	        MySQL         
   10.217.22.146（公网IP：111.7.74.34:80） 	            www.pyjrfw.com            	root/Pyzhjr12#$	   网站前端和app、展业助手后端    

生产环境发布流程：

1.首先登陆堡垒机VPN（https://111.6.98.20/vpn/user/portal/home）	用户名/密码：zhjr / Zhjr@123

第一次使用VPN需要下载驱动，安装完成后重新打开浏览器。

登陆成功后桌面右下角出现VPN图标则表示已经建立连接。

登录堡垒机打开堡垒机登录界面, 输入用户名，密码登录。

2.登陆网神SecFox运维安全管理与审计系统。

下载需要控件（必须安装系统控件）。

进入主页选择需要管理的资源，点击登录方式图标，填入目标资源的账号密码。

3.需要和各个开发人员确认代码在那个分支上，是否修改了配置文件。

4.puyang-platform(门户):需要本地打好war包（注意：datasource.propertise需要

使用生产环境的配置，注意包名），生产环境停掉Tomcat(jps - l)。

5.puyang-web:查看分支，拉取代码，打包，，先做好备份，删除，远程拷贝，启动。

puyang-wap：查看分支，拉取代码，打包，做好备份，删除，远程拷贝。(直接生效

)。注意：bigscreen，usermenu，weixin都在puyang-wap项目中。

6.大屏项目生产上线打包：79服务器上 /root/gateway/www路径下，执

行./package.sh,会生成bigscreen目录,将该目录放到 生产wap目录下。
