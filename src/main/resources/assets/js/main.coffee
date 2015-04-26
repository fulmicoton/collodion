React = require 'react'
Application = require  './components/application.cjsx'

$ ->
	React.render <Application/>, $("body")[0]
