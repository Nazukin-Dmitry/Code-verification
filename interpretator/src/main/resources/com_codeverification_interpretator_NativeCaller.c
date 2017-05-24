#include <jni.h>
#include <stdio.h>
#include "com_codeverification_interpretator_NativeCaller.h"
#include "ffi.h"
#include <Windows.h>
#include <string.h>

// Implementation of native method sayHello() of HelloJNI class
JNIEXPORT jobject JNICALL Java_com_codeverification_interpretator_NativeCaller_callNativeFunc
(JNIEnv * env, jobject thisObj, jstring lib, jstring func, jobjectArray args, jobjectArray argTypes, jstring retType) {

    char *funcC = (*env)->GetStringUTFChars(env, func, NULL);

    char *inCStr = (*env)->GetStringUTFChars(env, lib, NULL);
    if (NULL == inCStr) return NULL;
    HINSTANCE dllHandle = LoadLibrary(inCStr);

    ffi_cif cif;
    ffi_type *c_retType;

    jsize length = (*env)->GetArrayLength(env, args);

    ffi_type *ffi_argTypes[length];
    void *values[length];

    int i;
    for (i = 0; i < length; i++) {
        jobject argValue = (*env)->GetObjectArrayElement(env, args, i);
        jstring argType = (jstring) (*env)->GetObjectArrayElement(env, argTypes, i);
        if (NULL == argValue) return NULL;
        if (NULL == argType) return NULL;
        const char *argTypeC = (*env)->GetStringUTFChars(env, argType, NULL);

        jclass thisClass = (*env)->GetObjectClass(env, argValue);
        if (strcmp (argTypeC, "bool")==0) {
            jmethodID booleanValueMID = (*env)->GetMethodID(env, thisClass, "asBool", "()Z");
            if (NULL == booleanValueMID) return NULL;
            unsigned char* booleanValue = (unsigned char*) malloc(sizeof(unsigned char));
            *booleanValue = (unsigned char) (*env)->CallBooleanMethod(env, argValue, booleanValueMID);

            values[i] = booleanValue;
            ffi_argTypes[i] = &ffi_type_uchar;
        }
        if (strcmp (argTypeC, "byte")==0) {
            jmethodID methodId = (*env)->GetMethodID(env, thisClass, "asLong", "()J");
            if (NULL == methodId) return NULL;
            signed char* value = (signed char*) malloc(sizeof(signed char));
            *value = (signed char) (*env)->CallLongMethod(env, argValue, methodId);

            values[i] = value;
            ffi_argTypes[i] = &ffi_type_schar;
        }
        if (strcmp (argTypeC, "int")==0) {
            jmethodID methodId = (*env)->GetMethodID(env, thisClass, "asLong", "()J");
            if (NULL == methodId) return NULL;
            int* value = (int*) malloc(sizeof(int));
            *value = (int) (*env)->CallLongMethod(env, argValue, methodId);

            values[i] = value;
            ffi_argTypes[i] = &ffi_type_sint;
        }
        if (strcmp (argTypeC, "uint")==0) {
            jmethodID methodId = (*env)->GetMethodID(env, thisClass, "asLong", "()J");
            if (NULL == methodId) return NULL;
            unsigned int* value = (unsigned int*) malloc(sizeof(unsigned int));
            *value = (unsigned int) (*env)->CallLongMethod(env, argValue, methodId);

            values[i] = value;
            ffi_argTypes[i] = &ffi_type_uint;
        }
        if (strcmp (argTypeC, "long")==0) {
            jmethodID methodId = (*env)->GetMethodID(env, thisClass, "asLong", "()J");
            if (NULL == methodId) return NULL;
            long* value = (long*) malloc(sizeof(long));
            *value = (long) (*env)->CallLongMethod(env, argValue, methodId);

            values[i] = value;
            ffi_argTypes[i] = &ffi_type_slong;
        }
        if (strcmp (argTypeC, "ulong")==0) {
            jmethodID methodId = (*env)->GetMethodID(env, thisClass, "asLong", "()J");
            if (NULL == methodId) return NULL;
            unsigned long* value = (unsigned long*) malloc(sizeof(unsigned long));
            *value = (unsigned long) (*env)->CallLongMethod(env, argValue, methodId);

            values[i] = value;
            ffi_argTypes[i] = &ffi_type_ulong;
        }
        if (strcmp (argTypeC, "char")==0) {
            jmethodID methodId = (*env)->GetMethodID(env, thisClass, "asChar", "()C");
            if (NULL == methodId) return NULL;
            char* value = (char*) malloc(sizeof(char));
            *value = (char) (*env)->CallLongMethod(env, argValue, methodId);

            values[i] = value;
            ffi_argTypes[i] = &ffi_type_uchar;
        }
        if (strcmp (argTypeC, "string")==0) {
            jmethodID methodId = (*env)->GetMethodID(env, thisClass, "asString", "()Ljava/lang/String;");
            if (NULL == methodId) return NULL;
            jstring value = (jstring) (*env)->CallObjectMethod(env, argValue, methodId);

            const char *stringValue = (*env)->GetStringUTFChars(env, value, NULL);
            values[i] = &stringValue;
            ffi_argTypes[i] = &ffi_type_pointer;
        }
    }
    char *retTypeC = (*env)->GetStringUTFChars(env, retType, NULL);
    if (strcmp (retTypeC, "bool")==0) {
        c_retType = &ffi_type_uchar;
    }
    if (strcmp (retTypeC, "byte")==0) {
        c_retType = &ffi_type_schar;
    }
    if (strcmp (retTypeC, "int")==0) {
        c_retType = &ffi_type_sint;
    }
    if (strcmp (retTypeC, "uint")==0) {
        c_retType = &ffi_type_uint;
    }
    if (strcmp (retTypeC, "long")==0) {
        c_retType = &ffi_type_slong;
    }
    if (strcmp (retTypeC, "ulong")==0) {
        c_retType = &ffi_type_ulong;
    }
    if (strcmp (retTypeC, "char")==0) {
        c_retType = &ffi_type_uchar;
    }
    if (strcmp (retTypeC, "string")==0) {
        c_retType = &ffi_type_pointer;
    }

    ffi_arg rc; // return value
    if (ffi_prep_cif(&cif, FFI_DEFAULT_ABI, length, c_retType, ffi_argTypes) == FFI_OK)    {

        ffi_call(&cif, FFI_FN(GetProcAddress(dllHandle, funcC)), &rc, values);
    } else {
        return NULL;
    }

    if (strcmp (retTypeC, "bool")==0) {
        jclass cclass = (*env)->FindClass(env, "com/codeverification/interpretator/BoolValue");
        jmethodID constr = (*env)->GetMethodID(env, cclass, "<init>", "(Z)V");
        if (NULL == constr) return NULL;
        jobject result = (*env)->NewObject(env, cclass, constr, (jboolean) rc);
        return result;
    }
    if (strcmp (retTypeC, "byte")==0) {
        jclass cclass = (*env)->FindClass(env, "com/codeverification/interpretator/LongValue");
        jmethodID constr = (*env)->GetMethodID(env, cclass, "<init>", "(J)V");
        if (NULL == constr) return NULL;
        jobject result = (*env)->NewObject(env, cclass, constr, (jlong) rc);
        return result;
    }
    if (strcmp (retTypeC, "int")==0) {
        jclass cclass = (*env)->FindClass(env, "com/codeverification/interpretator/LongValue");
        jmethodID constr = (*env)->GetMethodID(env, cclass, "<init>", "(J)V");
        if (NULL == constr) return NULL;
        jobject result = (*env)->NewObject(env, cclass, constr, (jlong) rc);
        return result;
    }
    if (strcmp (retTypeC, "uint")==0) {
        jclass cclass = (*env)->FindClass(env, "com/codeverification/interpretator/LongValue");
        jmethodID constr = (*env)->GetMethodID(env, cclass, "<init>", "(J)V");
        if (NULL == constr) return NULL;
        jobject result = (*env)->NewObject(env, cclass, constr, (jlong) rc);
        return result;
    }
    if (strcmp (retTypeC, "long")==0) {
        jclass cclass = (*env)->FindClass(env, "com/codeverification/interpretator/LongValue");
        jmethodID constr = (*env)->GetMethodID(env, cclass, "<init>", "(J)V");
        if (NULL == constr) return NULL;
        jobject result = (*env)->NewObject(env, cclass, constr, (jlong) rc);
        return result;
    }
    if (strcmp (retTypeC, "ulong")==0) {
        jclass cclass = (*env)->FindClass(env, "com/codeverification/interpretator/LongValue");
        jmethodID constr = (*env)->GetMethodID(env, cclass, "<init>", "(J)V");
        if (NULL == constr) return NULL;
        jobject result = (*env)->NewObject(env, cclass, constr, (jlong) rc);
        return result;
    }
    if (strcmp (retTypeC, "char")==0) {
        jclass cclass = (*env)->FindClass(env, "com/codeverification/interpretator/CharValue");
        jmethodID constr = (*env)->GetMethodID(env, cclass, "<init>", "(C)V");
        if (NULL == constr) return NULL;
        jobject result = (*env)->NewObject(env, cclass, constr, (jchar) rc);
        return result;
    }
    if (strcmp (retTypeC, "string")==0) {
        jclass cclass = (*env)->FindClass(env, "com/codeverification/interpretator/StringValue");
        jmethodID constr = (*env)->GetMethodID(env, cclass, "<init>", "(Ljava/lang/String;)V");
        if (NULL == constr) return NULL;
        jobject result = (*env)->NewObject(env, cclass, constr, (*env)->NewStringUTF(env, (char *)rc));
        return result;
    }
    return NULL;
}