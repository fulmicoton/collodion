React = require 'react'
Staff = require './staff.cjsx'

LeftPane = React.createClass
	render: ->
		<div className='left-pane pane'>
		</div>

RightPane = React.createClass
	render: ->
		<div className='right-pane pane'>
			<Staff url="http://localhost:8080/api/corpus/1/processed" />
		</div>


Application = React.createClass
	render: ->
		<div id="main">
			<LeftPane/>
			<RightPane/>
		</div>

module.exports = Application 