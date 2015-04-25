React = require 'react'
Staff = require './staff.cjsx'

LeftPane = React.createClass
	getInitialState: ->
		{nbDocs: 1}
	url: ->
		"http://localhost:8080/api/corpus/"
	componentDidMount: ->
		$.getJSON @url(), (data)=>
			@setState data
	render: ->


Application = React.createClass
	
	getInitialState: ->
		{nbDocs: 1, docId: 0, tokens: []}
	
	url: ->
		"http://localhost:8080/api/corpus/"
	
	docUrl: (docId)->
	 	"http://localhost:8080/api/corpus/#{docId}/processed"


	tryGo: (docId)->
		if (docId >= 0) && (docId < @state.nbDocs)
			$.getJSON @docUrl(docId), (data)=>
				console.log "data", data
				@setState
					docId: docId
					tokens: data.tokens

	goNext: ->
		@tryGo @state.docId + 1
		
	goPrevious: ->
		@tryGo @state.docId - 1
		
	componentDidMount: ->
		$.getJSON @url(), (data)=>
			@setState data
		$(document).on 'keydown', (e)=>
		    if (e.which==39)
		    	@goNext()
		   	if (e.which == 37)
		   		@goPrevious()
		  	
	render: ->
		<div id="main">
			<div className='left-pane pane'>
				#{@state.docId} / #{@state.nbDocs}
			</div>
			<div className='right-pane pane'>
				<Staff tokens={@state.tokens}/>
			</div>
		</div>

module.exports = Application 