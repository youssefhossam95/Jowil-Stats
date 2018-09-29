import os
import re
os.chdir(os.getcwd()+'/../java/Jowil')

files = [f for f in os.listdir(os.getcwd()) if f.endswith(".java")]
count=0
for file in files:
    lines=open(file,'r').readlines()
    for line in lines:
        matchObj=re.search(r'showAlertAndWait\(Alert.AlertType.ERROR,stage.getOwner\(\),(.+),',line)
        if matchObj:
            print(matchObj.group(1)+':"translation",')
            count=count+1


print(count)
