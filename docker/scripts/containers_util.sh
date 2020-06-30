case $1 in
'start')
  docker stop $(docker ps -aq)
  ;;
'stop')
  docker start $(docker ps -aq)
  ;;
*)
  echo Must provide one of: start, stop
esac


