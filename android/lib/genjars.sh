#!/bin/sh

if [ $# -lt 1 ]; then
  echo "Usage: $0 <version>  1>&2"
  exit 1
fi


VERSION=$1
GOOGLE=/opt/android-sdk/extras/google/m2repository/com/google
AAR2JAR=/usr/local/bin/aar2jar.sh

#rm *.jar

#$AAR2JAR $GOOGLE/android/gms/play-services/$VERSION/play-services-$VERSION.aar .
#$AAR2JAR $GOOGLE/android/gms/play-services-ads/$VERSION/play-services-ads-$VERSION.aar .
$AAR2JAR $GOOGLE/android/gms/play-services-auth/$VERSION/play-services-auth-$VERSION.aar .
#$AAR2JAR $GOOGLE/android/gms/play-services-common/$VERSION/play-services-common-$VERSION.aar .
#$AAR2JAR $GOOGLE/android/gms/play-services-dynamic/$VERSION/play-services-dynamic-$VERSION.aar .
#$AAR2JAR $GOOGLE/android/gms/play-services-dynamite/$VERSION/play-services-dynamite-$VERSION.aar .

#$AAR2JAR $GOOGLE/android/gms/play-services-flags/$VERSION/play-services-flags-$VERSION.aar .
$AAR2JAR $GOOGLE/android/gms/play-services-gcm/$VERSION/play-services-gcm-$VERSION.aar .
$AAR2JAR $GOOGLE/android/gms/play-services-iid/$VERSION/play-services-iid-$VERSION.aar .
$AAR2JAR $GOOGLE/android/gms/play-services-base/$VERSION/play-services-base-$VERSION.aar .
$AAR2JAR $GOOGLE/android/gms/play-services-basement/$VERSION/play-services-basement-$VERSION.aar .
$AAR2JAR $GOOGLE/android/gms/play-services-tasks/$VERSION/play-services-tasks-$VERSION.aar .

$AAR2JAR $GOOGLE/android/gms/play-services-internal/$VERSION/play-services-internal-$VERSION.aar .
#$AAR2JAR $GOOGLE/android/gms/play-services-measurement/$VERSION/play-services-measurement-$VERSION.aar .
#$AAR2JAR $GOOGLE/android/gms/play-services-playlog/$VERSION/play-services-playlog-$VERSION.aar .
#$AAR2JAR $GOOGLE/android/gms/play-services-signin/$VERSION/play-services-signin-$VERSION.aar .

$AAR2JAR $GOOGLE/firebase/firebase-iid/$VERSION/firebase-iid-$VERSION.aar .

$AAR2JAR $GOOGLE/firebase/firebase-common/$VERSION/firebase-common-$VERSION.aar .



 ls -l *.jar
 