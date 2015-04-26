React = require 'react'
store = require '../store.coffee'

Token = React.createClass
	
	render: ->
		className = "token token-" + @props.data.type.toLowerCase()
		<div className={className}>
			<div className="text">{@props.data.charterm}</div>
			<div className="stem">{@props.data.stem}</div>
			<div className="number">{@props.data.number}</div>
		</div>

EmptyToken = React.createClass

	render: ->
		<div className="empty-token">{@props.text}</div>


Staff = React.createClass
	
	getInitialState: ->
		tokens: []
	
	componentDidMount: ->
		@unbinders = []
		onChange = =>
			@setState store.getDoc()
		@unbinders.push store.events.docChange.bind(onChange)
	componentWillUnmount: ->
		for unbinder in @unbinders
			@unbinder()

	render: ->
		tokenEls = []
		curOffset = 0
		for token in @state.tokens
			tokenEls.push <Token data={token} />
			curOffset = token.offset.end
		<div className="staff">
			{tokenEls}
		</div>

module.exports = Staff
