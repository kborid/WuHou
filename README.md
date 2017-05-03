Command line instructions

Git global setup

git config --global user.name "xxx"
git config --global user.email "xxx@xxx.com"

Create a new repository

git clone git@10.150.32.22:cd/ZaiChengDu_Android.git
cd ZaiChengDu_Android
touch README.md
git add README.md
git commit -m "add README"
git push -u origin master

Existing folder

cd existing_folder
git init
git remote add origin git@10.150.32.22:cd/ZaiChengDu_Android.git
git add .
git commit
git push -u origin master

Existing Git repository

cd existing_repo
git remote add origin git@10.150.32.22:cd/ZaiChengDu_Android.git
git push -u origin --all
git push -u origin --tags
