React = require 'react'


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
		{tokens: []}

	componentDidMount: ->
		$.getJSON @props.url, (data)=>
			@setState data
	render: ->
		tokenEls = []
		curOffset = 0
		for token in @state.tokens
			console.log token
			#if curOffset != token.offset.start
			#	tokenEls.push <EmptyToken text={@state.text.substring(curOffset, token.offset.start)} />
			tokenEls.push <Token data={token} />
			curOffset = token.offset.end
		<div>
			{tokenEls}
		</div>

module.exports = Staff
