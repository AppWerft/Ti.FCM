/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2011-2016 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

/** This is generated, do not edit by hand. **/

#include <jni.h>

#include "Proxy.h"

namespace ti {
namespace fcm {

class FcmModule : public titanium::Proxy
{
public:
	explicit FcmModule(jobject javaObject);

	static void bindProxy(v8::Local<v8::Object>, v8::Local<v8::Context>);
	static v8::Local<v8::FunctionTemplate> getProxyTemplate(v8::Isolate*);
	static void dispose(v8::Isolate*);

	static jclass javaClass;

private:
	static v8::Persistent<v8::FunctionTemplate> proxyTemplate;

	// Methods -----------------------------------------------------------
	static void cancelAll(const v8::FunctionCallbackInfo<v8::Value>&);
	static void cancel(const v8::FunctionCallbackInfo<v8::Value>&);
	static void init(const v8::FunctionCallbackInfo<v8::Value>&);
	static void setAppBadge(const v8::FunctionCallbackInfo<v8::Value>&);
	static void getAppBadge(const v8::FunctionCallbackInfo<v8::Value>&);
	static void setDebug(const v8::FunctionCallbackInfo<v8::Value>&);
	static void isRemoteNotificationsEnabled(const v8::FunctionCallbackInfo<v8::Value>&);
	static void isGooglePlayServicesAvailable(const v8::FunctionCallbackInfo<v8::Value>&);
	static void unregisterForPushNotifications(const v8::FunctionCallbackInfo<v8::Value>&);
	static void getRemoteDeviceUUID(const v8::FunctionCallbackInfo<v8::Value>&);
	static void cancelWithTag(const v8::FunctionCallbackInfo<v8::Value>&);
	static void getSenderId(const v8::FunctionCallbackInfo<v8::Value>&);
	static void checkPlayServices(const v8::FunctionCallbackInfo<v8::Value>&);
	static void registerForPushNotifications(const v8::FunctionCallbackInfo<v8::Value>&);

	// Dynamic property accessors ----------------------------------------
	static void getter_remoteNotificationsEnabled(v8::Local<v8::Name> name, const v8::PropertyCallbackInfo<v8::Value>& info);
	static void getter_remoteDeviceUUID(v8::Local<v8::Name> name, const v8::PropertyCallbackInfo<v8::Value>& info);

};

} // fcm
} // ti
